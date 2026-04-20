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
import org.springframework.data.domain.SliceImpl;
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

    FileEntity csvFile = null;

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

      csvFile = fileService.saveInternalData("backup_data.csv", "text/csv", csvData);

      // 백업 완료(COMPLETED 상태)
      backupTxService.complete(backupId, csvFile);

    } catch (Exception e) {
      // 백업 실패(FAILED) 상태
      // log 파일 생성, log 파일을 저장, 실패 상태로 전환
      String log = "Error : " + e.getMessage();
      byte[] logData = log.getBytes(StandardCharsets.UTF_8);

      // log 파일로 에러 로그 생성
      FileEntity logFile = fileService.saveInternalData("backup_error.log", "text/plain", logData);

      // 백업 실패(FAILED 상태)
      backupTxService.fail(backupId, logFile);

      // 백업 최종 실패 시 CSV파일이 남아있을 경우 삭제
      if (csvFile != null) {
        fileService.cleanupDummyFile(csvFile.getId());
      }

    }
  }

  // 백업 목록 조회
  // 100~1까지 100개 있다고 가정했을 때
  // 앞에서부터 10개 단위로 조회(DB에는 order by desc), 임시 content 개수는 11개(100~90)
  // hasNext를 통해 개수를 10개로 줄임(100~91)
  // last는 10개 중 마지막 번호인 91이 나옴
  // 추후 91보다 작은 id를 order by desc로 조회 -> 90번부터 조회
  // 임시 content 개수는 11개 ... 반복
  @Override
  @Transactional(readOnly = true)
  public PageResponse<BackupResponse> findAll(BackupSearchCondition condition) {

    // 페이지 size의 기본값(default)은 10
    int size = (condition.size() == null) ? 10 : condition.size();

    // size가 음수거나 0일 경우
    if (size <= 0) {
      size = 10;
    }

    // cursor(startedAt과 Id로 정렬)
    Instant cursorStartedAt = condition.cursorStartedAt();
    Long cursorId = condition.cursorId();

    // pageable의 개수는 10+1 11개
    Pageable pageable = PageRequest.of(0, size + 1);

    // DB 조회 pageable 개수만큼(11개) 조회
    Slice<Backup> slice = backupRepository.search(
        condition.worker(),
        condition.status(),
        condition.startFrom(),
        condition.startTo(),
        cursorStartedAt,
        cursorId,
        pageable
    );

    // pageable 수만큼, 10+1개 11개를 가져옴
    List<Backup> content = slice.getContent();

    // 조회 결과 개수 > size이면 true
    boolean hasNext = slice.hasNext();

    // 0~9번까지 10개
    if (hasNext) {
      content = content.subList(0, size);
    }

    // content가 비면 null(마지막 원소 도달), 그렇지 않으면 마지막 데이터를 가져옴
    Backup last = content.isEmpty() ? null : content.get(content.size() - 1);

    String nextCursor = null;

    if (last != null) {
      nextCursor = last.getStartedAt().toString() + "|" + last.getId();
    }

    // DTO 변환
    List<BackupResponse> mapped = content.stream().map(BackupResponse::from).toList();

    // Slice 형태로 응답 생성
    Slice<BackupResponse> responseSlice = new SliceImpl<>(mapped, pageable, hasNext);

    //
    return PageResponse.fromSlice(responseSlice, nextCursor,
        last != null ? last.getId() : null);
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
