package com.sb11.hr_bank.domain.backup.controller;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
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
  // 기존 : 백업 생성(POST) 메서드에서 @RequestParam으로 클라이언트의 IP를 클라이언트가 직접 작성하여 요청 / (머지 된 후 삭제할 주석)
  // 수정 : extractWorker 메서드를 사용하여 클라이언트가 직접 IP를 작성하는게 아닌(IP주소 조작 가능성을 배제) 서버에서 자동으로 추출하도록 설정 (머지 된 후 삭제할 주석)
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

    // X-FORWARDED-FOR는 클라이언트의 IP주소뿐만 아니라 프록시 IP도 가져올수 있다는데.. / (머지 된 후 삭제할 주석)
    // 첫번째 값은 클라이언트(사용자)의 IP주소, 두번째 값부터는 Proxy IP 주소가 나온다고 합니다. / (머지 된 후 삭제할 주석)
    // "168.0.0.1, 168.0.0.2, ..." 처럼 출력이 된다고 하네요.. / (머지 된 후 삭제할 주석)
    // 프록시 IP가 나올 가능성을 고려하여 클라이언트(사용자)의 IP 주소만 주입하도록 수정하였습니다.
    if (ip != null && !ip.isEmpty()) {
      return ip.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

}
