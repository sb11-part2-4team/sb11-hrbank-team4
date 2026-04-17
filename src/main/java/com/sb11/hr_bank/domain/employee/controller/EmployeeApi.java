package com.sb11.hr_bank.domain.employee.controller;

import com.sb11.hr_bank.domain.employee.dto.EmployeeCountCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCreateRequest;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeTrendCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeTrendDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeApi {

    @Operation(summary = "직원 등록")
    ResponseEntity<EmployeeDto> create(
            EmployeeCreateRequest dto,
            MultipartFile profile
    );

    @Operation(summary = "직원 상세 조회")
    ResponseEntity<EmployeeDto> findById(
            Long id
    );

    @Operation(summary = "직원 목록 조회")
    ResponseEntity<List<EmployeeDto>> findAll(
            EmployeeSearchCondition condition
    );

    @Operation(summary = "직원 수 조회")
    ResponseEntity<Long> count(
            EmployeeCountCondition condition
    );

    @Operation(summary = "직원 분포 조회")
    ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
            EmployeeDistributionCondition condition
    );

    @Operation(summary = "직원 수 추이 조회")
    ResponseEntity<List<EmployeeTrendDto>> getTrend(
            @ModelAttribute EmployeeTrendCondition condition
    );

    @Operation(summary = "직원 수정")
    ResponseEntity<Void> update(
            Long id,
            EmployeeUpdateRequest dto,
            MultipartFile profile
    );

    @Operation(summary = "직원 삭제")
    ResponseEntity<Void> delete(
            Long id
    );

}
