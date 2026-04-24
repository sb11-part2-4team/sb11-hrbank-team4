package com.sb11.hr_bank.domain.backup.dto;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record BackupResponse(

    @Schema(description = "백업 ID", example = "1")
    Long id,

    @Schema(description = "작업자", example = "127.0.0.1")
    String worker,

    @Schema(description = "백업 시작 시간")
    Instant startedAt,

    @Schema(description = "백업 완료 시간")
    Instant endedAt,

    @Schema(description = "백업 상태", example = "완료")
    BackupStatus status,

    @Schema(description = "백업 파일 ID", example = "1")
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