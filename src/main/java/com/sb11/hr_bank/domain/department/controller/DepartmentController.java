package com.sb11.hr_bank.domain.department.controller;

import com.sb11.hr_bank.domain.department.dto.DepartmentCreateRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentPageRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.domain.department.dto.DepartmentUpdateRequest;
import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.service.DepartmentService;
import com.sb11.hr_bank.global.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스가 외부 요청을 받는곳
@RequestMapping("/api/departments") // Swagger에 명시된 기본 주소를 설정
@RequiredArgsConstructor // final이 붙은 서비스를 자동으로 연결
public class DepartmentController implements DepartmentApi {

  private final DepartmentService departmentService; // 작업자인 서비스를 불러옴

  // 부서등록
  @Override
  @PostMapping
  public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
    Department department = Department.builder()
        .name(request.name())
        .description(request.description())
        .establishedDate(request.establishedDate())
        .build();

    // 서비스로부터 생성된 부서의 상세 정보를 받음
    DepartmentResponse response = departmentService.save(department);

    // 201 Created 코드와 생성된 데이터를 함께 반환
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 부서수정
  @Override
  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
    Department updateParam = Department.builder()
        .name(request.name())
        .description(request.description())
        .establishedDate(request.establishedDate()).build();

    DepartmentResponse response = departmentService.update(id, updateParam);
    // 성공 시 200 OK를 반환
    return ResponseEntity.ok(response);
  }

  // 부서삭제
  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    departmentService.delete(id);
    // 삭제 성공 시 204 No Content 반환 (RESTful 관례)
    return ResponseEntity.noContent().build();
  }

  // 부서 상세 조회
  @Override
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentResponse> getDepartmentDetail(@PathVariable Long id) {
    DepartmentResponse response = departmentService.getDepartmentDetail(id);
    // 데이터를 담아 200 OK와 함께 전송
    return ResponseEntity.ok(response);
  }

  // 전체 목록 조회
  @Override
  @GetMapping
  public ResponseEntity<PageResponse<DepartmentResponse>> getAllDepartments(@Valid @ModelAttribute DepartmentPageRequest request) {
    // DTO 내부의 변환 메서드를 통해 안전하게 Pageable을 생성
    Pageable pageable = request.toPageable();
    return ResponseEntity.ok(departmentService.findAll(pageable));
  }
}