package com.sb11.hr_bank.domain.department.dto;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "부서 상세 응답 정보")
public record DepartmentResponse(
    @Schema(description = "부서 ID", example = "1")
    Long id,

    @Schema(description = "부서명", example = "개발팀")
    String name,

    @Schema(description = "부서 설명", example = "소프트웨어 개발을 담당하는 부서입니다.")
    String description,

    @Schema(description = "설립일", example = "2023-01-01")
    LocalDate establishedDate,

    @Schema(description = "해당 부서 직원 수", example = "15")
    long employeeCount
) {
  public static DepartmentResponse from(Department department, List<Employee> employees) {
    return new DepartmentResponse(
        department.getId(),
        department.getName(),
        department.getDescription(),
        department.getCreatedDate(),
        employees != null ? employees.size() : 0
    );
  }
}