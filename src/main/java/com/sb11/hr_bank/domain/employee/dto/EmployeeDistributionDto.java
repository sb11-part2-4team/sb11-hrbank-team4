package com.sb11.hr_bank.domain.employee.dto;

public record EmployeeDistributionDto(
        String groupKey,
        Long count,
        Double percentage
) {
}
