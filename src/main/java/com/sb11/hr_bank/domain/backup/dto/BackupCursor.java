package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.time.Instant;

public record BackupCursor(
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    Long id
) {

}
