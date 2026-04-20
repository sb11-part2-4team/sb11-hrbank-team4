package com.sb11.hr_bank.domain.department.controller;

import com.sb11.hr_bank.domain.department.dto.DepartmentRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "부서 관리", description = "부서 등록, 수정, 삭제 및 상세 조회를 위한 API")
public interface DepartmentApi {

  @Operation(summary = "부서 등록")
  ResponseEntity<Void> createDepartment(@RequestBody DepartmentRequest request);

  @Operation(summary = "부서 수정")
  ResponseEntity<Void> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request);

  @Operation(summary = "부서 삭제")
  ResponseEntity<Void> deleteDepartment(@PathVariable Long id);

  @Operation(summary = "부서 상세 조회", description = "부서 정보와 함께 소속 직원 수 및 직원 명단을 반환합니다.")
  ResponseEntity<DepartmentResponse> getDepartmentDetail(@PathVariable Long id);

  @Operation(summary = "전체 부서 목록 조회")
  ResponseEntity<Page<DepartmentResponse>> getAllDepartments(Pageable pageable);
}