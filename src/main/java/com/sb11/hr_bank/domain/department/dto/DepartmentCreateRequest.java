package com.sb11.hr_bank.domain.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "부서 생성 요청 정보")
public record DepartmentCreateRequest(
    @NotBlank(message = "부서명은 필수입니다.")
    @Schema(description = "부서명", example = "개발팀", requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    @Schema(description = "부서 설명", example = "소프트웨어 개발을 담당하는 부서입니다.")
    String description,

    @NotNull(message = "설립일은 필수입니다.")
    @Schema(description = "설립일 (YYYY-MM-DD)", example = "2023-01-01", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate establishedDate
) { }