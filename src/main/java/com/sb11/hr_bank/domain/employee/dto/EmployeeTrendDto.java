package com.sb11.hr_bank.domain.employee.dto;

public record EmployeeTrendDto(
        String date,
        Long count,
        Long change,
        Double changeRate
) {
}
