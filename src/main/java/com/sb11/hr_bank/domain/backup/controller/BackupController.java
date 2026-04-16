package com.sb11.hr_bank.domain.backup.controller;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.service.BackupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

  // 데이터 백업 목록 조회
  @GetMapping
  public ResponseEntity<List<BackupResponse>> findAll() {
    return ResponseEntity.ok(backupService.findAll());
  }

  // 데이터 백업 생성
  @PostMapping
  public ResponseEntity<Void> startBackup(
      @RequestParam("worker") String worker) {
    backupService.startBackup(worker);
    return ResponseEntity.ok().build();
  }

  // 가장 최근 백업 조회
  @GetMapping("/latest")
  public ResponseEntity<BackupResponse> findLatest(
      @RequestParam(name = "status", defaultValue = "COMPLETED") BackupStatus status) {
    return ResponseEntity.ok(backupService.findLatest(status));
  }

}
