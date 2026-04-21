package com.sb11.hr_bank.domain.department.controller;

import com.sb11.hr_bank.domain.department.dto.DepartmentCreateRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentPageRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.domain.department.dto.DepartmentUpdateRequest;
import com.sb11.hr_bank.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "부서 관리", description = "부서 등록, 수정, 삭제 및 조회 API")
public interface DepartmentApi {

  @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "부서 등록 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 부서명입니다.")
  })
  @PostMapping
  ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentCreateRequest request);

  @Operation(summary = "부서 수정", description = "기존 부서의 정보를 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "부서 수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
      @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음"),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 부서명입니다.")
  })
  @PatchMapping("/{id}")
  ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request);

  @Operation(summary = "부서 삭제", description = "특정 부서를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "부서 삭제 성공 (내용 없음)"),
      @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음"),
      @ApiResponse(responseCode = "409", description = "해당 부서에 소속된 직원이 있어 삭제할 수 없습니다.")
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteDepartment(@PathVariable Long id);

  @Operation(summary = "부서 상세 조회", description = "특정 부서의 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "부서 상세 조회 성공"),
      @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음")
  })
  @GetMapping("/{id}")
  ResponseEntity<DepartmentResponse> getDepartmentDetail(@PathVariable Long id);

  @Operation(summary = "전체 부서 목록 조회", description = "페이징 처리된 전체 부서 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "전체 부서 목록 조회 성공")
  @GetMapping
  ResponseEntity<PageResponse<DepartmentResponse>> getAllDepartments(@Valid @ModelAttribute DepartmentPageRequest request);
}