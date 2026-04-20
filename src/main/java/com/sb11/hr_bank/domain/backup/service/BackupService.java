package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.global.dto.PageResponse;

public interface BackupService {

  // 백업 생성
  void startBackup(String worker);

  // 백업 목록 조회
  PageResponse<BackupResponse> findAll(BackupSearchCondition condition);

  // 가장 최신 백업 조회
  BackupResponse findLatest(BackupStatus status);
}
