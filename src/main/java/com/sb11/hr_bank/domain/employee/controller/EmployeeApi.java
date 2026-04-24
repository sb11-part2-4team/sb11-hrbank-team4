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
import com.sb11.hr_bank.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "직원 관리", description = "직원 관리 API")
public interface EmployeeApi {

    @Operation(summary = "직원 등록", description = "새로운 직원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일"),
            @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<EmployeeDto> create(
            EmployeeCreateRequest dto,
            MultipartFile profile
    );

    @Operation(summary = "직원 상세 조회", description = "직원 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "직원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<EmployeeDto> findById(
            Long id
    );

    @Operation(summary = "직원 목록 조회", description = "직원 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<PageResponse<EmployeeDto>> findAll(
            EmployeeSearchCondition condition
    );

    @Operation(summary = "직원 수 조회", description = "지정된 조건에 맞는 직원 수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<Long> count(
            EmployeeCountCondition condition
    );

    @Operation(summary = "직원 분포 조회", description = "지정된 기준으로 그룹화된 직원 분포를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 그룹화 기준"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
            EmployeeDistributionCondition condition
    );

    @Operation(summary = "직원 수 추이 조회", description = "지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 시간 단위"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<List<EmployeeTrendDto>> getTrend(
            @ModelAttribute EmployeeTrendCondition condition
    );

    @Operation(summary = "직원 수정", description = "직원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일"),
            @ApiResponse(responseCode = "404", description = "직원 또는 부서를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<EmployeeDto> update(
            Long id,
            EmployeeUpdateRequest dto,
            MultipartFile profile
    );

    @Operation(summary = "직원 삭제", description = "직원을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "직원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<Void> delete(
            Long id
    );

}
