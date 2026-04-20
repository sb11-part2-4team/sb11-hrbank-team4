package com.sb11.hr_bank.domain.department.dto;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "부서 상세 응답 정보")
public record DepartmentResponse(
    @Schema(description = "부서 ID")
    Long id,

    @Schema(description = "부서명")
    String departmentName,

    @Schema(description = "부서 설명")
    String departmentDescription,

    @Schema(description = "설립일")
    LocalDate establishmentDate,

    @Schema(description = "해당 부서 직원 수")
    long employeeCount,

    @Schema(description = "소속 직원 목록")
    List<EmployeeSummary> employees
) {
  public record EmployeeSummary(
      Long id,
      String name,
      String email,
      String position
  ) {}

  public static DepartmentResponse from(Department department, List<Employee> employees) {
    return new DepartmentResponse(
        department.getId(),
        department.getName(),
        department.getDescription(),
        department.getCreatedDate(), // Entity의 필드를 DTO의 establishmentDate로 매핑
        employees.size(),
        employees.stream()
            .map(e -> new EmployeeSummary(e.getId(), e.getName(), e.getEmail(), e.getPosition()))
            .toList()
    );
  }
}