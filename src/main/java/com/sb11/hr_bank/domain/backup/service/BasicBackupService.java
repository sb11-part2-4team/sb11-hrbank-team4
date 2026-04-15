package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.repository.BackupRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicBackupService implements BackupService {
  private final BackupRepository backupRepository;

  @Override
  public List<Backup> findAll() {
    return null;
  }

}
