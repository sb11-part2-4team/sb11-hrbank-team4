package com.sb11.hr_bank.domain.department.service;

import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;

  // 부서 등록 기능 구현

  @Transactional
  public DepartmentResponse save(Department department) {
    if (departmentRepository.existsByName(department.getName())) {
      throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
    } // 입렵한 부서명이 이미 DB에 있는지 확인하고 중복 메세지 구현

    Department saved = departmentRepository.save(department);
    return DepartmentResponse.from(saved, List.of());
  } // 중복이 없으면 DB에 저장


  // 부서 수정 기능 구현

  @Transactional
  public DepartmentResponse update(Long id, Department updateParam) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 부서가 없습니다. id=" + id));
    // 먼저 수정할 부서가 DB에 있는지 확인하고 가져옴

    if (updateParam.getName() != null && !department.getName().equals(updateParam.getName())) {
      if (departmentRepository.existsByName(updateParam.getName())) {
        throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
      }
      department.setName(updateParam.getName());
    }

    if (updateParam.getDescription() != null) department.setDescription(updateParam.getDescription());
    if (updateParam.getCreatedDate() != null) department.setCreatedDate(updateParam.getCreatedDate());

    List<Employee> employees = employeeRepository.findByDepartmentId(id);
    return DepartmentResponse.from(department, employees);
  }
    // 부서명을 바꾸려고 할 때, 이미 다른 부서에서 쓰고 있는 부서명인지 체크



  // 부서삭제 기능

  @Transactional
  public void delete(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다."));

    if (employeeRepository.existsByDepartmentId(id)) {
      throw new IllegalStateException("해당 부서에 소속된 직원이 있어 삭제할 수 없습니다.");
    }
    departmentRepository.delete(department);
  }

  public DepartmentResponse getDepartmentDetail(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 부서가 없습니다. id=" + id));
    List<Employee> employees = employeeRepository.findByDepartmentId(id);
    return DepartmentResponse.from(department, employees);
  }

  public PageResponse<DepartmentResponse> findAll(Pageable pageable) {
    Page<Department> departmentPage = departmentRepository.findAll(pageable);
    List<Department> departments = departmentPage.getContent();

    List<Long> deptIds = departments.stream().map(Department::getId).toList();
    List<Employee> allEmployees = employeeRepository.findByDepartmentIdIn(deptIds);

    Map<Long, List<Employee>> employeeMap = allEmployees.stream()
        .collect(Collectors.groupingBy(e -> e.getDepartment().getId()));

    List<DepartmentResponse> content = departments.stream()
        .map(dept -> DepartmentResponse.from(dept, employeeMap.getOrDefault(dept.getId(), List.of())))
        .toList();

    return new PageResponse<>(content, departmentPage);
  }
}