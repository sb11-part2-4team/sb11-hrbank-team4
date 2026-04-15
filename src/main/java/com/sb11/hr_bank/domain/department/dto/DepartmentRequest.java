package com.sb11.hr_bank.domain.department.dto;

import java.time.LocalDate;

public record DepartmentRequest(
    String name,
    String description,
    LocalDate createdDate
) { }