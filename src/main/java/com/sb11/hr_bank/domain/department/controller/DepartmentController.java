package com.sb11.hr_bank.domain.department.controller;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.service.DepartmentService;
import com.sb11.hr_bank.domain.department.dto.DepartmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 이 클래스가 외부 요청을 받는곳
@RequestMapping("/sb/hrbank/api/departments") // Swagger에 명시된 기본 주소를 설정
@RequiredArgsConstructor // final이 붙은 서비스를 자동으로 연결
public class DepartmentController {

  private final DepartmentService departmentService; // 작업자인 서비스를 불러옴

// 부서등록

  @PostMapping // 데이터를 새로 저장할 때 사용
  public Long createDepartment(@RequestBody DepartmentRequest request) {
    // 화면에서 보낸 JSON 데이터를 DTO 바구니에 담아 받음
    // DTO에 담긴 내용을 꺼내 새로운 Entity로 만듬
    Department department = Department.builder()
        .name(request.name())
        .description(request.description())
        .createdDate(request.createdDate())
        .build();

    // 완성된 내용을 서비스에게 전달후 DB에 저장
    return departmentService.save(department);
  }

// 부서수정
  @PutMapping("/{id}") // 기존 데이터 수정할때 사용
  public void updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request) {
    // 수정할 데이터를 DTO에서 가져와서 Entity형태로 전환
    Department updateParam = Department.builder()
        .name(request.name())
        .description(request.description())
        .createdDate(request.createdDate())
        .build();

      departmentService.update(id, updateParam);
      // DepartmentService에 요청, id의 내용을 수정
  }

// 부서삭제

  @DeleteMapping("/{id}")
  // 데이터를 삭제할 때 사용

  public void deleteDepartment(@PathVariable Long id) {
    departmentService.delete(id);

  // 삭제할 대상의 ID를 요청후 서비스에게 해당 번호의 부서를 삭제하라고 요청
  }

  // 부서 목록 조회

  @GetMapping // 데이터를 조회시 사용
  public List<Department> getAllDepartments() {
    // 서비스에게 모든 부서 목록을 불러오게 요청
    return departmentService.findAll();
  }
}