package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.time.Instant;

public record BackupSearchCondition(
    String worker,
    Instant startFrom,
    Instant startTo,
    BackupStatus status,

    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {

}
