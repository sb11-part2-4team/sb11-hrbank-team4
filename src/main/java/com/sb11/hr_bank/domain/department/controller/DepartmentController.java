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

  @PostMapping // 데이터를 새로 저장할 때 사용하는 방식
  public Long createDepartment(@RequestBody DepartmentRequest request) {
    // DTO에 담긴 내용을 꺼내 새로운 Entity로 만듬
    Department department = Department.builder()
        .name(request.name())
        .description(request.description())
        .createdDate(request.createdDate())
        .build();

    // 완성된 내용을 서비스에게 전달후 DB에 저장
    return departmentService.save(department);
  }

  @GetMapping // 데이터를 조회시 사용
  public List<Department> getAllDepartments() {
    // 서비스에게 모든 부서 목록을 불러오게 요청
    return departmentService.findAll();
  }
}