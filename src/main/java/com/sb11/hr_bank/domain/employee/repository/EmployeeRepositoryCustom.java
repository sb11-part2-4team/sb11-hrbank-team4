package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionDto;

import java.util.List;

public interface EmployeeRepositoryCustom {
    List<EmployeeDistributionDto> findDistribution(EmployeeDistributionCondition condition);
}
