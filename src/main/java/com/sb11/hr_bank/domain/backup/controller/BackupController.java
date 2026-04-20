package com.sb11.hr_bank.domain.backup.controller;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.service.BackupService;
import com.sb11.hr_bank.global.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
  public ResponseEntity<PageResponse<BackupResponse>> findAll(
      @ModelAttribute BackupSearchCondition condition
  ) {
    return ResponseEntity.ok(
        backupService.findAll(condition));
  }

  // 데이터 백업 생성
  @PostMapping
  public ResponseEntity<Void> startBackup(
      HttpServletRequest request) {
    String worker = extractWorker(request);
    backupService.startBackup(worker);
    return ResponseEntity.ok().build();
  }

  // 가장 최근 백업 조회(상태별 조회)
  // 상태 기본값은 COMPLETED
  @GetMapping("/latest")
  public ResponseEntity<BackupResponse> findLatest(
      @RequestParam(name = "status", defaultValue = "COMPLETED") BackupStatus status) {
    return ResponseEntity.ok(backupService.findLatest(status));
  }

  // 클라이언트의 IP 주소를 가져오는 메서드
  private String extractWorker(HttpServletRequest request) {
    String ip = request.getHeader("X-FORWARDED-FOR"); // X-FORWARDED-FOR : 클라이언트 IP주소를 가져옴

    // 프록시 IP가 나올 가능성을 고려하여 클라이언트(사용자)의 IP 주소만 주입하도록 설정
    if (ip != null && !ip.isEmpty()) {
      return ip.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

}
