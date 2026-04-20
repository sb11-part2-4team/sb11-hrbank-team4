package com.sb11.hr_bank.domain.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "부서 생성 및 수정 요청 정보")
public record DepartmentRequest(
    @Schema(description = "부서명", example = "개발팀")
    String departmentName,

    @Schema(description = "부서 설명", example = "서비스 백엔드 개발 담당")
    String departmentDescription,

    @Schema(description = "설립일", example = "2024-01-01")
    String establishmentDate
) { }