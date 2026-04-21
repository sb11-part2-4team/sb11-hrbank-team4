package com.sb11.hr_bank.domain.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "부서 생성 및 수정 요청 정보")
public record DepartmentRequest(
    @Schema(description = "부서명", example = "개발팀", requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    @Schema(description = "부서 설명", example = "소프트웨어 개발을 담당하는 부서입니다.")
    String description,

    @Schema(description = "설립일 (YYYY-MM-DD)", example = "2023-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    String establishedDate
) { }