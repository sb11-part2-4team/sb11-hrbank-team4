package com.sb11.hr_bank.domain.changelogs.controller;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.service.ChangeLogService;
import com.sb11.hr_bank.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  // 직원 정보 수정 이력 목록 조회
  @GetMapping
  public ResponseEntity<PageResponse<ChangeLogResponseDto.ListInfo>> getLogList(
      @ModelAttribute ChangeLogRequestDto.Search searchRequest
  ) {
    PageResponse<ChangeLogResponseDto.ListInfo> response = changeLogService.getLogList(searchRequest);
    return ResponseEntity.ok(response);
  }


  // 직원 정보 수정 이력 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ChangeLogResponseDto.DetailInfo> getLogDetail(
      @PathVariable("id") Long id
  ) {
      ChangeLogResponseDto.DetailInfo response = changeLogService.getLogDetail(id);

      // 200 OK 응답
      return ResponseEntity.ok(response);
  }

  // 수정이력 건수 조회
  @GetMapping("/count")
  public ResponseEntity<Long> getLogCount(
      @RequestParam(required = false) Instant fromDate,
      @RequestParam(required = false) Instant toDate
  ) {
    // fromDate > toDate 검증
    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      return ResponseEntity.badRequest().build();
    }
    long count = changeLogService.getLogCount(fromDate, toDate);
    return ResponseEntity.ok(count);
  }


}
