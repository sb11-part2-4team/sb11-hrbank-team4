package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.time.Instant;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    FileEntity file
) {

}
