package com.sb11.hr_bank.domain.employee.dto;

import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmployeeUpdateRequest(

        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @NotNull
        Long departmentId,

        @NotBlank
        String position,

        @NotNull
        LocalDate hireDate,

        @NotNull
        EmployeeStatus status
) {
}
