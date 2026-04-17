package com.sb11.hr_bank.domain.backup.scheduler;

import com.sb11.hr_bank.domain.backup.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackupScheduler {

  private final BackupService backupService;

  // 3600.000초? = 1시간
  // 배치 주기는 애플리케이션 설정을 통한 주입
  // 1시간마다 백업 생성 메서드 설정(검사 여부는 서비스에서 처리)
  @Scheduled(fixedRateString = "${backup.interval-ms}")
  public void runBackupJob() {
    backupService.startBackup("system");
  }
}
