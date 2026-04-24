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

  // 부서 등록 기능 구현
  @Transactional
  public DepartmentResponse save(Department department) {
    // 입력한 부서명이 이미 DB에 있는지 확인하고 중복 예외(409) 발생
    if (departmentRepository.existsByName(department.getName())) {
      throw new BusinessException(ErrorCode.DEPARTMENT_DUPLICATE_NAME);
    }

    Department saved = departmentRepository.save(department);
    return DepartmentResponse.from(saved, List.of());
  }

  // 부서 수정 기능 구현
  @Transactional
  public DepartmentResponse update(Long id, Department updateParam) {
    // 수정할 부서가 DB에 있는지 확인 (없으면 404 예외)
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

    // 부서명을 바꾸려고 할 때, 이미 다른 부서에서 쓰고 있는 부서명인지 체크
    if (updateParam.getName() != null && !department.getName().equals(updateParam.getName())) {
      if (departmentRepository.existsByName(updateParam.getName())) {
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
        .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

    // 해당 부서에 소속된 직원이 있는지 확인 후 삭제 불가 예외(409) 발생
    if (employeeRepository.existsByDepartmentId(id)) {
      throw new BusinessException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
    }

    departmentRepository.delete(department);
  }

  // 부서 상세 조회
  public DepartmentResponse getDepartmentDetail(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

    List<Employee> employees = employeeRepository.findByDepartmentId(id);
    return DepartmentResponse.from(department, employees);
  }


  public PageResponse<DepartmentResponse> findAll(Pageable pageable, String nameOrDescription, Long idAfter) {
    Page<Department> departmentPage;

    boolean hasSearch = (nameOrDescription != null && !nameOrDescription.trim().isEmpty());

    if (hasSearch && idAfter != null) {
      departmentPage = departmentRepository.findByIdLessThanAndNameContainingOrIdLessThanAndDescriptionContainingOrderByIdDesc(
          idAfter, nameOrDescription, idAfter, nameOrDescription, pageable);
    } else if (hasSearch) {
      departmentPage = departmentRepository.findByNameContainingOrDescriptionContaining(
          nameOrDescription, nameOrDescription, pageable);
    } else if (idAfter != null) {
      departmentPage = departmentRepository.findByIdLessThanOrderByIdDesc(idAfter, pageable);
    } else {
      departmentPage = departmentRepository.findAll(pageable);
    }

    List<Department> departments = departmentPage.getContent();

    // 조회된 부서가 없으면 빠른 응답 반환
    if (departments.isEmpty()) {
      return new PageResponse<>(
          List.of(),
          null,
          null,
          departmentPage.getSize(),
          departmentPage.getTotalElements(),
          false
      );
    }

    List<Long> deptIds = departments.stream().map(Department::getId).toList();
    List<Employee> allEmployees = employeeRepository.findByDepartmentIdIn(deptIds);

    // 부서 ID를 기준으로 직원 목록 그룹화
    Map<Long, List<Employee>> employeeMap = allEmployees.stream()
        .collect(Collectors.groupingBy(e -> e.getDepartment().getId()));

    List<DepartmentResponse> content = departments.stream()
        .map(dept -> DepartmentResponse.from(dept, employeeMap.getOrDefault(dept.getId(), List.of())))
        .toList();

    Long nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).id();

    return new PageResponse<>(
        content,
        null,
        nextIdAfter,
        departmentPage.getSize(),
        departmentPage.getTotalElements(),
        departmentPage.hasNext()
    );
  }
}