package com.sb11.hr_bank.domain.department.controller;

import com.sb11.hr_bank.domain.department.dto.DepartmentRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "부서 관리", description = "부서 등록, 수정, 삭제 및 상세 조회를 위한 API")
public interface DepartmentApi {

  @Operation(summary = "부서 등록")
  ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request);

  @Operation(summary = "부서 수정")
  ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request);

  @Operation(summary = "부서 삭제")
  ResponseEntity<Void> deleteDepartment(@PathVariable Long id);

  @Operation(summary = "부서 상세 조회")
  ResponseEntity<DepartmentResponse> getDepartmentDetail(@PathVariable Long id);

  @Operation(summary = "전체 부서 목록 조회")
  ResponseEntity<PageResponse<DepartmentResponse>> getAllDepartments(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  );
}