package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.time.Instant;

public record BackupResponse(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    Long fileId
) {

  public static BackupResponse from(Backup backup) {
    return new BackupResponse(
        backup.getId(),
        backup.getWorker(),
        backup.getStartedAt(),
        backup.getEndedAt(),
        backup.getStatus(),
        backup.getFile() != null ? backup.getFile().getId() : null
    );
  }
}
// mapper 클래스로 분리할지 ?
// 정적 팩토리 메서드로 작성할지 ?