package com.sb11.hr_bank.domain.employee.dto;

public record EmployeeCursor(
        String sortField,
        String value,
        Long idAfter,
        String sortDirection
) {
}
