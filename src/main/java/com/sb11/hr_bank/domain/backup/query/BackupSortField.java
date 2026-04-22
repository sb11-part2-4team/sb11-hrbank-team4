package com.sb11.hr_bank.domain.backup.query;

import com.fasterxml.jackson.annotation.JsonValue;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;

public enum BackupSortField {
  STARTED_AT("startedAt"),
  ENDED_AT("endedAt"),
  STATUS("status");

  private final String value;

  BackupSortField(String value) {
    this.value = value;
  }

  public static BackupSortField from(String value) {
    for (BackupSortField field : values()) {
      if (field.value.equals(value)) {
        return field;
      }
    }
    throw new BusinessException(ErrorCode.BACKUP_INVALID_SORT_FIELD);
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
