package com.sb11.hr_bank.domain.employee.dto;

import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record EmployeeSearchCondition(

        @Size(max = 100)
        String nameOrEmail,

        @Size(max = 100)
        String employeeNumber,

        String departmentName,

        @Size(max = 100)
        String position,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate hireDateFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate hireDateTo,

        EmployeeStatus status,

        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {
}
