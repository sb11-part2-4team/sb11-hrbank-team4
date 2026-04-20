package com.sb11.hr_bank.domain.department.controller;

import com.sb11.hr_bank.domain.department.dto.DepartmentRequest;
import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.service.DepartmentService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스가 외부 요청을 받는곳
@RequestMapping("/sb/hrbank/api/departments") // Swagger에 명시된 기본 주소를 설정
@RequiredArgsConstructor // final이 붙은 서비스를 자동으로 연결
public class DepartmentController implements DepartmentApi {

  private final DepartmentService departmentService; // 작업자인 서비스를 불러옴

  // 부서등록
  @PostMapping // 데이터를 새로 저장할 때 사용
  public ResponseEntity<Void> createDepartment(DepartmentRequest request) {
    // 화면에서 보낸 JSON 데이터를 DTO 바구니에 담아 받음
    // DTO에 담긴 내용을 꺼내 새로운 Entity로 만듬
    Department department = Department.builder()
        .name(request.departmentName())
        .description(request.departmentDescription())
        .createdDate(LocalDate.parse(request.establishmentDate()))
        .build();

    departmentService.save(department);
    // 201 Created 상태코드와 함께 저장된 ID를 반환
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // 부서수정
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request) {
    // 수정할 데이터를 DTO에서 가져와서 Entity형태로 전환
    Department updateParam = Department.builder()
        .name(request.departmentName())
        .description(request.departmentDescription())
        .createdDate(LocalDate.parse(request.establishmentDate()))
        .build();

    departmentService.update(id, updateParam);
    // 성공 시 200 OK를 반환
    return ResponseEntity.ok().build();
  }

  // 부서삭제
  @DeleteMapping("/{id}") // 데이터를 삭제할 때 사용
  public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    departmentService.delete(id);
    // 삭제 성공 시 200 OK를 반환합니다
    return ResponseEntity.ok().build();
  }

  // 부서 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentResponse> getDepartmentDetail(@PathVariable Long id) {
    DepartmentResponse response = departmentService.getDepartmentDetail(id);
    // 데이터를 담아 200 OK와 함께 전송
    return ResponseEntity.ok(response);
  }

  // 전체 목록 조회
  @GetMapping
  public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(Pageable pageable) {
    Page<DepartmentResponse> responses = departmentService.findAll(pageable);
    return ResponseEntity.ok(responses);
  }
}