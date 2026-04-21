package com.sb11.hr_bank.domain.backup.query;

import com.fasterxml.jackson.annotation.JsonValue;

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
    return null;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
