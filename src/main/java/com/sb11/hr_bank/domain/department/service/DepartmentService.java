package com.sb11.hr_bank.domain.department.service;

import com.sb11.hr_bank.domain.department.dto.DepartmentResponse;
import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
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

  // 부서 등록 기능
  @Transactional
  public DepartmentResponse save(Department department) {
    if (departmentRepository.existsByName(department.getName())) {
      // 중복 시 예외 처리 (409 Conflict)
      throw new BusinessException(ErrorCode.DEPARTMENT_DUPLICATE_NAME);
    }

    Department saved = departmentRepository.save(department);
    return DepartmentResponse.from(saved, List.of());
  }

  // 부서 수정 기능
  @Transactional
  public DepartmentResponse update(Long id, Department updateParam) {
    Department department = departmentRepository.findById(id)
        // 부서 없음 예외 처리 (404 Not Found)
        .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

    if (updateParam.getName() != null && !department.getName().equals(updateParam.getName())) {
      if (departmentRepository.existsByName(updateParam.getName())) {
        // 부서명 중복 시 예외 처리 (409 Conflict)
        throw new BusinessException(ErrorCode.DEPARTMENT_DUPLICATE_NAME);
      }
      department.setName(updateParam.getName());
    }

    if (updateParam.getDescription() != null) {
      department.setDescription(updateParam.getDescription());
    }

    if (updateParam.getEstablishedDate() != null) {
      department.setEstablishedDate(updateParam.getEstablishedDate());
    }

    List<Employee> employees = employeeRepository.findByDepartmentId(id);
    return DepartmentResponse.from(department, employees);
  }

  // 부서 삭제 기능
  @Transactional
  public void delete(Long id) {
    Department department = departmentRepository.findById(id)
        // 부서 없음 예외 처리 (404 Not Found)
        .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

    if (employeeRepository.existsByDepartmentId(id)) {
      // 삭제 불가 예외 처리 (409 Conflict)
      throw new BusinessException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
    }

    departmentRepository.delete(department);
  }

  // 부서 상세 조회
  public DepartmentResponse getDepartmentDetail(Long id) {
    Department department = departmentRepository.findById(id)
        // 부서 없음 예외 처리 (404 Not Found)
        .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

    List<Employee> employees = employeeRepository.findByDepartmentId(id);
    return DepartmentResponse.from(department, employees);
  }

  // 부서 목록 조회
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


    return new PageResponse<>(
        content,
        null,
        null,
        departmentPage.getSize(),
        departmentPage.getTotalElements(),
        departmentPage.hasNext()
    );
  }
}