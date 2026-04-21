package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.query.BackupSortDirection;
import com.sb11.hr_bank.domain.backup.query.BackupSortField;
import java.time.Instant;

public record BackupSearchCondition(
    String worker,
    Instant startFrom,
    Instant startTo,
    BackupStatus status,

    BackupCursor cursor,
    Integer size,
    BackupSortField sortField,
    BackupSortDirection sortDirection

) {

  public BackupSearchCondition from(BackupCursor cursor) {
    return new BackupSearchCondition(
        worker,
        startFrom,
        startTo,
        status,
        cursor,
        size,
        sortField,
        sortDirection
    );
  }
}
