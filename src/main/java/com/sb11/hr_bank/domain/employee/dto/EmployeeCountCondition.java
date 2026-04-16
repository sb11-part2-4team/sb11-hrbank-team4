package com.sb11.hr_bank.domain.employee.dto;

import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record EmployeeCountCondition(
        EmployeeStatus status,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fromDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate toDate
) {
}
