package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.query.BackupSortDirection;
import com.sb11.hr_bank.domain.backup.query.BackupSortField;
import java.time.Instant;

public record BackupSearchCondition(
    String worker,
    Instant startedAtFrom,
    Instant startedAtTo,
    BackupStatus status,
    Long idAfter,
    String cursor,
    Integer size,
    BackupSortField sortField,
    BackupSortDirection sortDirection

) {

  public BackupSearchCondition withSize(int size) {
    return new BackupSearchCondition(
        worker,
        startedAtFrom,
        startedAtTo,
        status,
        idAfter,
        cursor,
        size,
        sortField,
        sortDirection
    );
  }
}
