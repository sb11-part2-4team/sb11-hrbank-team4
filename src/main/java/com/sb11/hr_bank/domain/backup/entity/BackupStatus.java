package com.sb11.hr_bank.backup.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BackupStatus {
  IN_PROGRESS("진행중"), COMPLETED("완료"), FAILED("실패"), SKIPPED("건너뜀");

  private final String description;
}
// Backup의 상태, 진행중(처리), 완료, 실패, 건너뜀(변경 이력이 없을 시)