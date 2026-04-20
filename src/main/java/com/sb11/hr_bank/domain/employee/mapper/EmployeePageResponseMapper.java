package com.sb11.hr_bank.domain.employee.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmployeePageResponseMapper {

    private final EmployeeMapper employeeMapper;
    private final ObjectMapper objectMapper;

    public PageResponse<EmployeeDto> toPageResponse(
            List<Employee> employees,
            EmployeeSearchCondition condition,
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

        Employee last = hasNext && !pageContent.isEmpty()
                ? pageContent.get(pageContent.size() - 1)
                : null;

        return new PageResponse<>(
                content,
                last != null ? encodeCursor(last, condition) : null,
                last != null ? last.getId() : null,
                size,
                totalElements,
                hasNext
        );

    }

    private String encodeCursor(Employee employee, EmployeeSearchCondition condition) {
        String sortField = sortField(condition);

        Map<String, Object> cursor = new LinkedHashMap<>();
        cursor.put(sortField, cursorValue(employee, sortField));

        try {
            byte[] json = objectMapper.writeValueAsBytes(cursor);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.EMPLOYEE_CURSOR_ENCODING_FAILED);
        }
    }

    private String sortField(EmployeeSearchCondition condition) {
        if(condition == null || condition.sortField() == null || condition.sortField().isBlank()) {
            return "name";
        }

        return switch (condition.sortField()) {
            case "name", "employeeNumber", "hireDate" -> condition.sortField();
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_SORT_FIELD);
        };
    }

    private String cursorValue(Employee employee, String sortField) {
        return switch (sortField) {
            case "name" -> employee.getName();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "hireDate" -> employee.getHireDate().toString();
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_SORT_FIELD);
        };
    }

}
