package com.sb11.hr_bank.domain.backup.repository;

import com.sb11.hr_bank.domain.backup.dto.BackupCursor;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.Backup;
import org.springframework.data.domain.Slice;

public interface BackupRepositoryCustom {

  Slice<Backup> search(BackupSearchCondition condition, BackupCursor cursor);

}
