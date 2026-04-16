package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;

public interface BackupService {

  // 백업 실행
  void startBackup(String worker);

  // 백업 목록 조회
  BackupResponse findAll();

  // 가장 최신 백업 조회
  BackupResponse findLatest();
}
