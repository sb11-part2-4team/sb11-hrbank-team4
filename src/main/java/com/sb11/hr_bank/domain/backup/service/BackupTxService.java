package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.repository.BackupRepository;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.service.FileService;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BackupTxService {

  private final BackupRepository backupRepository;
  private final FileService fileService;

  // 백업 상태 진행중으로 저장
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Long createInProgress(String worker) {
    Backup backup = Backup.create(worker);
    return backupRepository.save(backup).getId();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void complete(Long backupId, FileEntity csvFile) {
    Backup backup = backupRepository.findById(backupId).orElseThrow(
        () -> new BusinessException(ErrorCode.BACKUP_NOT_FOUND)
    );

    backup.complete(csvFile);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void fail(Long backupId, FileEntity logFile) {
    Backup backup = backupRepository.findById(backupId).orElseThrow(
        () -> new BusinessException(ErrorCode.BACKUP_NOT_FOUND)
    );

    // TODO 추후 실패시 만들었던 파일 삭제로직 호출하기

    backup.fail(logFile);

  }

}
