package com.sb11.hr_bank.domain.changelogs.controller;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.service.ChangeLogService;
import com.sb11.hr_bank.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 변경 이력 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  // 직원 정보 수정 이력 목록 조회
  @Operation(summary = "직원 정보 수정 이력 목록 조회", description = "조건에 맞는 변경 이력 목록을 커서 기반 페이징으로 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 커서 ID (CHANGELOG_INVALID_CURSOR)")
  })
  @GetMapping
  public ResponseEntity<PageResponse<ChangeLogResponseDto.ListInfo>> getLogList(
      @ModelAttribute ChangeLogRequestDto.Search searchRequest
  ) {
    PageResponse<ChangeLogResponseDto.ListInfo> response = changeLogService.getLogList(searchRequest);
    return ResponseEntity.ok(response);
  }


  // 직원 정보 수정 이력 상세 조회
  @Operation(summary = "직원 정보 수정 이력 상세 조회", description = "특정 변경 이력의 상세 내역(변경 전/후 데이터)을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "해당 이력을 찾을 수 없음 (CHANGELOG_NOT_FOUND)")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ChangeLogResponseDto.DetailInfo> getLogDetail(
      @PathVariable Long id
  ) {
      ChangeLogResponseDto.DetailInfo response = changeLogService.getLogDetail(id);

      // 200 OK 응답
      return ResponseEntity.ok(response);
  }

  // 수정이력 건수 조회
  @Operation(summary = "수정 이력 건수 조회", description = "특정 기간 내의 변경 이력 총 건수를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "시작일이 종료일보다 늦음 (잘못된 요청 파라미터)")
  })
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
