package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.mapper.BackupMapper;
import com.sb11.hr_bank.domain.backup.repository.BackupRepository;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import com.sb11.hr_bank.domain.file.service.FileService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private final BackupRepository backupRepository;
  private final EmployeeRepository employeeRepository;
  private final FileRepository fileRepository;
  private final BackupMapper backupMapper;


  @Override
  public void startBackup(String worker) {
    // 가장 최근 백업 시간을 가져옴(백업이 한번도 진행되지 않았을 때는 Instant.Min)
    Instant lastBackupTime = backupRepository.findLastCompletedTime().orElse(Instant.MIN);

    // 백업이 필요한지 유무를 파악, 사원의 변경 유무가 존재하는지
    boolean needBackup = employeeRepository.existsByUpdatedAtAfter(lastBackupTime);

    // 백업이 필요하지 않을 경우(이미 백업 진행 후에 변경 이력이 없을 경우)
    // 백업 건너뜀(SKIPPED 상태)
    if (!needBackup) {
      Backup skipped = Backup.skip(worker);
      backupRepository.save(skipped);
      return;
    }

    // 백업 시작(IN_PROGRESS 상태)
    Backup backup = Backup.create(worker);
    backupRepository.save(backup);

    FileEntity file = null;
    try {

      // CSV 파일로 백업 데이터를 생성
      file = FileService.createCsvBackup();

      // CSV 파일을 저장
      fileRepository.save(file);

      // 백업 완료(COMPLETED 상태)
      backup.complete(file);

    } catch (Exception e) {
      // 백업 실패(FAILED) 상태
      backup.fail();
    }

  }

  @Override
  public BackupResponse findAll() {

  }

  @Override
  public BackupResponse findLatest() {
    Backup backup = backupRepository.findTopByStatusOrderByEndedAtDesc(BackupStatus.COMPLETED)
        .orElseThrow(
            () -> new IllegalArgumentException("완료된 백업이 없습니다.")
        );

    return BackupResponse.from(backup);
  }
}
