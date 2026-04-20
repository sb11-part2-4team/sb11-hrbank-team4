package com.sb11.hr_bank.domain.department.service;

import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 이클래스가 핵심 비지니스 로직을 처리하는 서비스계층임을 선언
@RequiredArgsConstructor // final이 붙은 레포지토리를 스프링이 자동으로 연결
@Transactional(readOnly = true) // 테이터를 "읽기만" 하는 작업에서 성능 최적화

public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;

  // 부서 등록 기능 구현

  @Transactional // 데이터를 저장하므로 "읽기전용"을해제 트랜잭션을 적용

  public DepartmentResponse save(Department department) {
    if (departmentRepository.existsByName(department.getName())) {
      throw new IllegalArgumentException("이미 존재하는 부서명 입니다");
    } // 입렵한 부서명이 이미 DB에 있는지 확인하고 중복 메세지 구현
    Department savedDepartment = departmentRepository.save(department);

    return DepartmentResponse.from(savedDepartment, List.of());
  } // 중복이 없으면 DB에 저장

  // 부서 수정 기능 구현

  @Transactional
  public DepartmentResponse update(Long id, Department updateParam) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 부서가 없습니다. id=" + id));
    // 먼저 수정할 부서가 DB에 있는지 확인하고 가져옴

    if (!department.getName().equals(updateParam.getName()) &&
        departmentRepository.existsByName(updateParam.getName())) {
      throw new IllegalArgumentException("이미 존재하는 부서명 입니다");
    }
    // 부서명을 바꾸려고 할 때, 이미 다른 부서에서 쓰고 있는 부서명인지 체크

    department.setName(updateParam.getName());
    department.setDescription(updateParam.getDescription());
    department.setCreatedDate(updateParam.getCreatedDate());

    List<Employee> employees = employeeRepository.findByDepartmentId(id);
    return DepartmentResponse.from(department, employees);
  }

  // 부서삭제 기능

  @Transactional
  public void delete(Long id) {
    // 삭제할 부서가 현재 존재하는지 확인
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 부서가 없습니다. id=" + id));

    // 삭제할 부서에 현재 소속 직원이 있는지 확인
    if (employeeRepository.existsByDepartmentId(id)) {
      throw new IllegalStateException("해당 부서에 소속된 직원이 있어 삭제할 수 없습니다.");
    }

    // 삭제할 부서의 현재 소속된 직원이 없을 때만 실제 삭제 실행
    departmentRepository.delete(department);
  }

  // 부서상세조회 기능
  public DepartmentResponse getDepartmentDetail(Long id) {
    // 요청시 부서 정보를 DB에서 찾음. 없으면 예외 메세지를 보냄
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 부서가 없습니다. id=" + id));

    // 직원 레포지토리에게 해당 부서의 ID를 가진 모든 직원들을 찾아오라고 시킴
    List<Employee> employees = employeeRepository.findByDepartmentId(id);

    return DepartmentResponse.from(department, employees);
  }

  // 전체 부서 목록 조회 (페이지네이션 적용)

  public PageResponse<DepartmentResponse> findAll() {
    PageRequest pageRequest = PageRequest.of(0, 10);
    Page<Department> departments = departmentRepository.findAll(pageRequest);

    List<DepartmentResponse> content = departments.map(dept -> {
      List<Employee> employees = employeeRepository.findByDepartmentId(dept.getId());
      return DepartmentResponse.from(dept, employees);
    }).getContent();

    return new PageResponse<>(content, departments);
  }
}