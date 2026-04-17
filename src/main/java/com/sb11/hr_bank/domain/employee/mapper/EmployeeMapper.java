package com.sb11.hr_bank.domain.employee.mapper;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeDto toDto(Employee employee) {
        Department department = employee.getDepartment();

        Long profileId = null;
        FileEntity profile = employee.getProfileImage();
        if(profile != null) {
            profileId = profile.getId();
        }

        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getEmployeeNumber(),
                department.getId(),
                department.getName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getEmployeeStatus().getLabel(),
                profileId
        );
    }
}
