package com.sb11.hr_bank.domain.employee.service;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCreateRequest;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.fileentity.entity.FileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeDto create(EmployeeCreateRequest dto, FileEntity file) {
        if(employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Duplicate email");
        }

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        LocalDate hireDate = dto.hireDate();
        long count = employeeRepository.countByHireDateBetween(
                LocalDate.of(hireDate.getYear(), 1, 1),
                LocalDate.of(hireDate.getYear(), 12, 31)
        );
        String employeeNumber = String.format("EMP-%d-%03d", hireDate.getYear(), count + 1);

        Employee employee = new Employee(
                dto.name(),
                dto.email(),
                employeeNumber,
                department,
                dto.position(),
                dto.hireDate(),
                file
        );

        employeeRepository.save(employee);

        return toDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return toDto(employee);
    }

//    public List<EmployeeDto> findAllByCondition(); 추가 예정

    public void update(Long id, EmployeeUpdateRequest dto, FileEntity file) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        FileEntity fileEntity = file != null ? file : employee.getProfileImage();

        employee.update(
                dto.name(),
                dto.email(),
                department,
                dto.position(),
                dto.hireDate(),
                dto.status(),
                fileEntity
        );
    }

    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepository.deleteById(id);
    }

    private EmployeeDto toDto(Employee employee) {
        Department department = employee.getDepartment();

        Long profileId = null;
        FileEntity profile = employee.getProfileImage();
        if(profile != null) {
            profileId = profile.getId();
        }

        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getEmployeeNumber(),
                department.getId(),
                department.getName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getEmployeeStatus().getLabel(),
                profileId
        );
    }
}