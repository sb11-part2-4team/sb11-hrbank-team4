package com.sb11.hr_bank.domain.backup.controller;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @Tag : 도메인 이름과 도메인 설명
// @Opreation : 어떤 역할을 하는지 요약과 상세
// @ApiResponses : 2개 이상의 @ApiResponse를 사용할 경우 사용
// @ApiResponse : 발생하는 응답코드와 응답코드에 대한 설명
// @Parameter : 매개변수에 대한 이름과 설명

@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
public interface BackupApi {

  // 데이터 백업 목록 조회
  @Operation(summary = "데이터 백업 목록 조회", description = "데이터 백업 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청(커서, 정렬, 상태값)입니다."),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @GetMapping
  ResponseEntity<PageResponse<BackupResponse>> findAll(
      @Parameter(name = "condition", description = "검색 조건")
      @ModelAttribute BackupSearchCondition condition
  );

  // 데이터 백업 생성
  @Operation(summary = "데이터 백업 생성", description = "데이터 백업을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "백업 생성 성공 또는 건너뜀"),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
      @ApiResponse(responseCode = "409", description = "이미 백업이 진행중입니다."),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PostMapping
  ResponseEntity<BackupResponse> startBackup(
      HttpServletRequest request
  );

  // 가장 최근 백업 조회(상태별 조회)
  // 상태 기본값은 COMPLETED
  @Operation(summary = "최근 백업 조회", description = "가장 최근 백업 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 상태입니다."),
      @ApiResponse(responseCode = "404", description = "백업을 찾을 수 없습니다."),
      @ApiResponse(responseCode = "409", description = "현재 상태에서는 요청을 처리할 수 없습니다."),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @GetMapping("/latest")
  ResponseEntity<BackupResponse> findLatest(
      @Parameter(name = "status", description = "백업 상태. 미지정시 COMPLETED(완료)")
      @RequestParam(name = "status", defaultValue = "COMPLETED") BackupStatus status
  );
}
