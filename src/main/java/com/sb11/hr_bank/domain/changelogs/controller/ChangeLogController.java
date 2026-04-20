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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/changelogs")
public class ChangeLogController {

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
    String ip = httpRequest.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = httpRequest.getRemoteAddr();
    }
    // X-Forwarded-For가 여러 IP를 쉼표로 구분 해서 보낼 경우, 첫번째(진짜 클라이언트 IP)만 가져옴
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }

}
