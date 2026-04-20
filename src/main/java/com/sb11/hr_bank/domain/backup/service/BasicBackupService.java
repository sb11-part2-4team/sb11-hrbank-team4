package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.repository.BackupRepository;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.service.FileService;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private final BackupRepository backupRepository;
  private final ChangeLogRepository changeLogRepository;
  private final EmployeeRepository employeeRepository;

  private final FileService fileService;
  private final BackupTxService backupTxService;


  @Override
  public void startBackup(String worker) {
    // 가장 최근 백업 시간을 가져옴, 백업이 없을 경우 Optional.empty
    Optional<Instant> lastBackupTime =
        backupRepository.findTopByStatusOrderByEndedAtDesc(BackupStatus.COMPLETED)
            .map(Backup::getEndedAt);

    // 백업이 필요한지 유무를 파악하는 변수, 사원의 변경 유무가 존재하는지
    // lastBackupTime이 없을 경우(첫번째 백업) true를 반환하여 백업 처리 시작
    boolean needBackup = lastBackupTime.map(
        changeLogRepository::existsByCreatedAtAfter).orElse(true);

    // 백업이 필요하지 않을 경우(이미 백업 진행 후에 변경 이력이 없을 경우)
    // 백업 건너뜀(SKIPPED 상태)
    if (!needBackup) {
      Backup skipped = Backup.skip(worker);
      backupRepository.save(skipped);
      return;
    }

    // 백업 시작(IN_PROGRESS 상태, 트랜잭션)
    Long backupId = backupTxService.createInProgress(worker);

    FileEntity file;

    try {
      // 정상적으로 백업 성공

      // CSV 파일 형식
      // ID,직원번호,이름,이메일,부서,직급,입사일,상태
      // 144,EMP-2026-21410784000001,정채원,정채원45@gmail.com,백엔드 개발팀33,테크 리드,2025-09-10,ACTIVE

      // 사원 전체 데이터를 호출
      List<Employee> employees = employeeRepository.findAllWithDepartment();

      // 직원의 입사일(hireDate)을 YYYY-MM-DD의 형태로 변환
      // YYYY는 목요일을 기준으로 연도가 작년, 내년이 될 수 있음, yyyy는 정상적으로 연도 출력
      // MM은 월 수, mm은 분(시간)
      // DD는 연도 기준 일 수(1~365,366), dd는 월 기준 일 수
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      // StringBuilder를 사용하여 백업 데이터를 생성
      StringBuilder sb = new StringBuilder();

      // 헤더 생성(어떤 속성들 순서대로 넣을지)
      // id, employeeNumber, name, email, department, position, hireData, status 순서대로
      sb.append("ID,직원번호,이름,이메일,부서,직급,입사일,상태\n");

      for (Employee employee : employees) {
        sb.append(employee.getId()).append(",")
            .append(employee.getEmployeeNumber()).append(",")
            .append(escape(employee.getName())).append(",")
            .append(escape(employee.getEmail())).append(",")
            .append(escape(employee.getDepartment().getName())).append(",")
            .append(escape(employee.getPosition())).append(",")
            .append(employee.getHireDate().format(formatter)).append(",")
            .append(employee.getEmployeeStatus()).append("\n");
      }

      // CSV 파일로 사원 백업 데이터를 CSV 파일로 변환
      byte[] csvData = sb.toString().getBytes(StandardCharsets.UTF_8);

      file = fileService.saveInternalData("backup_data.csv", "text/csv", csvData);

      // 백업 완료(COMPLETED 상태)
      backupTxService.complete(backupId, file);

    } catch (Exception e) {
      // 백업 실패(FAILED) 상태
      // log 파일 생성, log 파일을 저장, 실패 상태로 전환
      String log = "Error : " + e.getMessage();
      byte[] logData = log.getBytes(StandardCharsets.UTF_8);

      // log 파일로 에러 로그 생성
      file = fileService.saveInternalData("backup_error.log", "text/plain", logData);

      // 백업 실패(FAILED 상태)
      backupTxService.fail(backupId, file);

      // 백업 최종 실패 시 CSV파일이 남아있을 경우 삭제
      if (file != null) {
        // TODO 추후 실패시 만들었던 파일 삭제로직 호출하기
      }

    }
  }

  // 백업 목록 조회
  @Override
  @Transactional(readOnly = true)
  public PageResponse<BackupResponse> findAll(BackupSearchCondition condition) {
    Pageable pageable = PageRequest.of(0, 10);

    Slice<Backup> slice = backupRepository.search(condition, pageable);

    Long nextIdAfter = slice.hasNext() ?
        slice.getContent().get(slice.getNumberOfElements() - 1).getId() : null;

    return PageResponse.fromSlice(slice.map(BackupResponse::from), null, nextIdAfter);
  }

  // 가장 최근의 백업을 조회(상태별 조회)
  // 상태 지정은 Controller에서(기본값 COMPLETED)
  @Override
  @Transactional(readOnly = true)
  public BackupResponse findLatest(BackupStatus status) {
    Backup backup = backupRepository.findTopByStatusOrderByEndedAtDesc(status)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.BACKUP_NOT_FOUND)
        );

    return BackupResponse.from(backup);
  }

  // csv 입력값 특수문자 처리(줄바꿈, 쉼표, 따옴표 등 처리)
  private String escape(String value) {
    if (value == null) {
      return "";
    }

    // 따옴표, 줄바꿈, 쉼표, \r(윈도우 계열 데이터에서 \r이 남을 수 있음)를 공백으로 처리
    value = value.replace("\"", " ");
    value = value.replace("\r", " ");
    value = value.replace("\n", " ");
    value = value.replace(",", " ");

    return value;
  }
}
