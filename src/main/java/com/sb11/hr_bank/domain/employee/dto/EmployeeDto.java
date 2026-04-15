package com.sb11.hr_bank.domain.employee.dto;

import java.time.LocalDate;

public record EmployeeDto(
        Long id,
        String name,
        String email,
        String employeeNumber,
        Long departmentId,
        String departmentName,
        String position,
        LocalDate hireDate,
        String status,
        Long profileImageId
) {
}