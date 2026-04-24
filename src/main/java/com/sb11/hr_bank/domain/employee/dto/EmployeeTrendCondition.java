package com.sb11.hr_bank.domain.employee.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record EmployeeTrendCondition(

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,

        String unit
) {
}
