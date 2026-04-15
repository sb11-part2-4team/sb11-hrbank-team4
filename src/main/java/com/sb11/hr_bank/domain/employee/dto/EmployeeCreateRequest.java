package com.sb11.hr_bank.domain.employee.dto;

import java.time.LocalDate;

public record EmployeeCreateRequest(
        String name,
        String email,
        Long departmentId,
        String position,
        LocalDate hireDate
) {
}
