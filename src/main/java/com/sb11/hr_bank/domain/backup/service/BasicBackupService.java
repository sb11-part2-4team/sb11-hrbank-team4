package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.repository.BackupRepository;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import com.sb11.hr_bank.domain.file.service.FileService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {

  private final BackupRepository backupRepository;
  private final ChangeLogRepository changeLogRepository;
  private final FileRepository fileRepository;

  private final FileService fileService;


  @Override
  @Transactional
  public void startBackup(String worker) {
    // 가장 최근 백업 시간을 가져옴(백업이 한번도 진행되지 않았을 때는 Instant.MIN)
    Instant lastBackupTime =
        backupRepository.findTopByStatusOrderByEndedAtDesc(BackupStatus.COMPLETED)
            .map(Backup::getEndedAt)
            .orElse(Instant.MIN);

    // 백업이 필요한지 유무를 파악하는 변수, 사원의 변경 유무가 존재하는지
    boolean needBackup = changeLogRepository.existsByCreatedAtAfter(lastBackupTime);

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

    FileEntity file;

    try {
      // 정상적으로 백업 성공
      // CSV 파일 생성, CSV 파일을 저장, 성공 상태로 전환

      // CSV 파일로 백업 데이터를 생성
      file = fileService.createCsvBackup();

      // CSV 파일을 저장(fileService에서 해당 메서드를 사용했다면 제거할 예정입니다)
      fileRepository.save(file);

      // 백업 완료(COMPLETED 상태)
      backup.complete(file);

    } catch (Exception e) {
      // 백업 실패(FAILED) 상태
      // log 파일 생성, log 파일을 저장, 실패 상태로 전환

      // log 파일로 에러 로그 생성
      file = fileService.createLogBackup();

      // log 파일을 저장(fileService에서 해당 메서드를 사용했다면 제거할 예정입니다)
      fileRepository.save(file);

      // 백업 실패(FAILED 상태)
      backup.fail(file);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<BackupResponse> findAll() {
    return backupRepository.findAll().stream()
        .map(BackupResponse::from)
        .toList();
  }

  // 가장 최근의 백업을 조회(상태별 조회)
  // 상태 지정은 Controller에서(기본값 COMPLETED)
  @Override
  @Transactional(readOnly = true)
  public BackupResponse findLatest(BackupStatus status) {
    Backup backup = backupRepository.findTopByStatusOrderByEndedAtDesc(status)
        .orElseThrow(
            () -> new IllegalArgumentException("완료된 백업이 없습니다.")
        );

    return BackupResponse.from(backup);
  }
}
