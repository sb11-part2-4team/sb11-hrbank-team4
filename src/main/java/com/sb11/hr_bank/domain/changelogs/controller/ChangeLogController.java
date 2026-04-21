package com.sb11.hr_bank.domain.changelogs.controller;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.service.ChangeLogService;
import com.sb11.hr_bank.global.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Set;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/changelogs")
public class ChangeLogController {

  private static final Set<String> TRUSTED_PROXIES = Set.of("127.0.0.1", "0:0:0:0:0:0:0:1", "::1");

  private final ChangeLogService changeLogService;

  // 변경 이력 등록
  @PostMapping
  public ResponseEntity<Void> createLog(@RequestBody ChangeLogRequestDto.Create request, HttpServletRequest httpRequest) {

    // 클라이언트 IP 주소 추출
    String ipAddress = getClientIp(httpRequest);
    changeLogService.createLog(request, ipAddress);

    // 201 Created 응답
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // 변경 이력 목록 조회
  @GetMapping
  public ResponseEntity<PageResponse<ChangeLogResponseDto.ListInfo>> getLogList(
      @ModelAttribute ChangeLogRequestDto.Search searchRequest
  ) {
    PageResponse<ChangeLogResponseDto.ListInfo> response = changeLogService.getLogList(searchRequest);

    // 200 OK
    return ResponseEntity.ok(response);
  }

  // 변경 이력 상세 조회
  @GetMapping("/{changeLogId}")
  public ResponseEntity<ChangeLogResponseDto.DetailInfo> getLogDetail(@PathVariable Long changeLogId) {
    ChangeLogResponseDto.DetailInfo response = changeLogService.getLogDetail(changeLogId);

    // 200 OK 응답
    return ResponseEntity.ok(response);
  }

  // IP 추출을 위한 Private Helper Method
  private String getClientIp(HttpServletRequest httpRequest) {

    String remoteAddr = httpRequest.getRemoteAddr();
    String forwardedFor = httpRequest.getHeader("X-Forwarded-For");

    // 신뢰 가능 프록시에서 온 요청일 때만 X-Forwarded-For 사용
    if (!TRUSTED_PROXIES.contains(remoteAddr) || forwardedFor == null || forwardedFor.isBlank()) {
      return remoteAddr;
    }

    return Arrays.stream(forwardedFor.split(","))
        .map(String::trim)
        .filter(token -> !token.isEmpty())
        .filter(token -> !"unknown".equalsIgnoreCase(token))
        .findFirst()
        .orElse(remoteAddr);
  }

}
