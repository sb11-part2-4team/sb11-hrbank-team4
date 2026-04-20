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
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import com.sb11.hr_bank.domain.file.service.FileService;
import com.sb11.hr_bank.global.dto.PageResponse;
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
  private final FileRepository fileRepository;
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
      // CSV 파일 생성, CSV 파일을 저장, 성공 상태로 전환

      // CSV 파일로 백업 데이터를 생성
      List<Employee> employees = employeeRepository.findAll();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      StringBuilder sb = new StringBuilder();
      sb.append("ID,직원번호,이름,이메일,부서,직급,입사일,상태\n");
      for (Employee employee : employees) {
        sb.append(employee.getId()).append(",")
            .append(employee.getEmployeeNumber()).append(",")
            .append(escapeCsv(employee.getName())).append(",")
            .append(escapeCsv(employee.getEmail())).append(",")
            .append(escapeCsv(employee.getDepartment().getName())).append(",")
            .append(escapeCsv(employee.getPosition())).append(",")
            .append(employee.getHireDate().format(formatter)).append(",")
            .append(employee.getEmployeeStatus()).append("\n");
      }
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
            () -> new IllegalArgumentException(status.getDescription() + " 상태의 백업이 없습니다.")
        );

    return BackupResponse.from(backup);
  }

  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }
    value = value.replace("\"", " ");
    value = value.replace("\r", " ");
    value = value.replace("\n", " ");
    value = value.replace(",", " ");
    return value;
  }
}