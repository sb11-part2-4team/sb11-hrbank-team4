package com.sb11.hr_bank.domain.employee.mapper;

import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeePageResponseMapper {

    private final EmployeeMapper employeeMapper;

    public PageResponse<EmployeeDto> toPageResponse(
            List<Employee> employees,
            int size,
            Long totalElements,
            boolean hasNext
    ) {
        List<Employee> pageContent = hasNext
                ? employees.subList(0, size)
                : employees;

        List<EmployeeDto> content = pageContent.stream()
                .map(employeeMapper::toDto)
                .toList();

        return new PageResponse<>(
                content,
                null,
                null,
                size,
                totalElements,
                hasNext
        );

    }

}
