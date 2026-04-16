package com.sb11.hr_bank.domain.department.dto;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import java.time.LocalDate;
import java.util.List;

public record DepartmentResponse(
    Long id,
    String departmentName,
    String departmentDescription,
    LocalDate establishmentDate,
    long employeeCount,           // 직원 수
    List<EmployeeSummary> employees // 상세 리스트
) {
  // 내부 클래스나 별도 파일로 직원의 핵심 정보만 담는 DTO
  public record EmployeeSummary(
      Long id,
      String name,
      String email,
      String position
  ) {}

  // Entity들을 DTO로 변환해주는 편의 메서드
  public static DepartmentResponse from(Department department, List<Employee> employees) {
    return new DepartmentResponse(
        department.getId(),
        department.getName(),
        department.getDescription(),
        department.getCreatedDate(),
        employees.size(), // 리스트 개수로 직원 수 계산
        employees.stream()
            .map(e -> new EmployeeSummary(e.getId(), e.getName(), e.getEmail(), e.getPosition()))
            .toList()
    );
  }
}