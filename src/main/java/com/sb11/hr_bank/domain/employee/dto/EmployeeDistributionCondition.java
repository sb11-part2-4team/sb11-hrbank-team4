package com.sb11.hr_bank.domain.employee.dto;

import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;

public record EmployeeDistributionCondition(
        String groupBy,
        EmployeeStatus status
) {
}
