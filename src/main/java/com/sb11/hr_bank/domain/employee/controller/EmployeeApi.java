package com.sb11.hr_bank.domain.employee.controller;

import com.sb11.hr_bank.domain.employee.dto.EmployeeCreateRequest;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EmployeeApi {

    @Operation(summary = "직원 등록")
    public ResponseEntity<EmployeeDto> create(
            EmployeeCreateRequest dto,
            MultipartFile profile
    ) throws IOException;

    @Operation(summary = "직원 상세 조회")
    public ResponseEntity<EmployeeDto> findById(
            Long employeeId
    );

    @Operation(summary = "직원 수정")
    public ResponseEntity<Void> update(
            Long employeeId,
            EmployeeUpdateRequest dto,
            MultipartFile profile
    ) throws IOException;

    @Operation(summary = "직원 삭제")
    public ResponseEntity<Void> delete(
            Long employeeId
    );

}
