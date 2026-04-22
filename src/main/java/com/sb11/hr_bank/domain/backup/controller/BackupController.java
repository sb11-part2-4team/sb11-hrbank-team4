package com.sb11.hr_bank.domain.backup.controller;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.service.BackupService;
import com.sb11.hr_bank.global.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;
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
public class BackupController implements BackupApi {

  // ipv4패턴(XXX.XXX.XXX.XXX)
  // 25(0~9), 200~240, 100~199, 0~99내의 숫자들을 3번씩
  private static final Pattern IPV4_PATTERN = Pattern.compile(
      "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)"
          + "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$");

  private static final String UNKNOWN_IP = "unknown";
  private static final String SYSTEM = "system";

  private final BackupService backupService;

  // 데이터 백업 목록 조회
  @Override
  @GetMapping
  public ResponseEntity<PageResponse<BackupResponse>> findAll(
      @ModelAttribute BackupSearchCondition condition
  ) {

    return ResponseEntity.ok(
        backupService.findAll(condition));
  }

  // 데이터 백업 생성
  @Override
  @PostMapping
  public ResponseEntity<BackupResponse> startBackup(
      HttpServletRequest request) {
    String worker = normalizeWorker(extractWorker(request));
    BackupResponse response = backupService.startBackup(worker);
    return ResponseEntity.ok(response);
  }

  // 가장 최근 백업 조회(상태별 조회)
  // 상태 기본값은 COMPLETED
  @Override
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

  // 작업자가 누구인지 결정
  // ip 주소가 빌 경우 system(스케쥴러), ipv4가 들어오면 ipv4주소, 나머지(ipv6)가 들어올 경우 ipv6 address가 됨
  private String normalizeWorker(String ip) {
    // ip주소가 없을 경우(스케쥴러 등)
    if (ip == null || ip.isBlank()) {
      return SYSTEM;
    }

    // localhost에서 테스트 시 IPV6 루프백 주소를 IPV4 주소로 변환
    if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
      return "127.0.0.1";
    }

    // IPV4 주소가 들어올 경우
    if (IPV4_PATTERN.matcher(ip).matches()) {
      return ip;
    }

    // IPV6 주소가 나올 경우 해시값으로 변환하여 길이 축약
    if (ip.contains(":")) {
      return hashIp(ip);
    }

    // IP주소 형태가 아닌 값이 나올 경우
    return UNKNOWN_IP;
  }

  // IPV6 주소를 해시함수로 변환
  private String hashIp(String ip) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(ip.getBytes(StandardCharsets.UTF_8));

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 8; i++) {
        sb.append(String.format("%02x", digest[i]));
      }
      return sb.toString();
    } catch (Exception e) {
      return UNKNOWN_IP;
    }
  }

}
