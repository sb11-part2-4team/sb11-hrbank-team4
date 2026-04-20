package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionRow;
import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;

import java.util.List;

public interface EmployeeRepositoryCustom {
    List<EmployeeDistributionRow> findDistribution(String groupBy, EmployeeStatus status);
}
