package com.sb11.hr_bank.domain.backup.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BackupStatus {
  IN_PROGRESS("진행중"), COMPLETED("완료"), FAILED("실패"), SKIPPED("건너뜀");

  private final String description;

  // 역직렬화(한글 -> 영어 / 예시 : "진행중"을 다시 IN_PROGRESS로)
  @JsonCreator
  public static BackupStatus fromDescription(String description) {
    for (BackupStatus status : values()) {
      if (status.getDescription().equals(description)) {
        return status;
      }
    }
    throw new BusinessException(ErrorCode.BACKUP_INVALID_STATUS);
  }

  // 직렬화(영어 -> 한글 / 예시 : IN_PROGRESS 프론트에서는 "진행중")
  @JsonValue
  public String getDescription() {
    return description;
  }

}
// Backup의 상태, 진행중(처리), 완료, 실패, 건너뜀(변경 이력이 없을 시)