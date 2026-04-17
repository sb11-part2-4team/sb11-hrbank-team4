package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.file.dto.FileResponse;
import java.time.Instant;

public record BackupResponse(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    FileResponse file
) {

  public static BackupResponse from(Backup backup) {
    return new BackupResponse(
        backup.getId(),
        backup.getWorker(),
        backup.getStartedAt(),
        backup.getEndedAt(),
        backup.getStatus(),
//        backup.getFile() != null ? FileResponse.from(backup.getFile()) // 정적 팩토리 메서드로 작성할 시 교체 예정
        backup.getFile() != null ?
            new FileResponse(backup.getFile().getId(),
                backup.getFile().getName(), backup.getFile().getContentType(),
                backup.getFile().getSize()) : null
    );
  }
}
// mapper 클래스로 분리할지 ?
// 정적 팩토리 메서드로 작성할지 ?