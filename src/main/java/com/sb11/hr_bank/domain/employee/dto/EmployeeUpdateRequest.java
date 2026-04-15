package com.sb11.hr_bank.domain.employee.dto;

import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
        String name,
        String email,
        Long departmentId,
        String position,
        LocalDate hireDate,
        EmployeeStatus status
) {
}
