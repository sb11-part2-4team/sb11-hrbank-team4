package com.sb11.hr_bank.domain.employee.dto;

import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeUpdateRequest(

        @NotBlank
        @Size(max = 20)
        String name,

        @Email
        @NotBlank
        @Size(max = 100)
        String email,

        @NotNull
        @Positive
        Long departmentId,

        @NotBlank
        @Size(max = 100)
        String position,

        @NotNull
        LocalDate hireDate,

        @NotNull
        EmployeeStatus status,

        String memo
) {
}
