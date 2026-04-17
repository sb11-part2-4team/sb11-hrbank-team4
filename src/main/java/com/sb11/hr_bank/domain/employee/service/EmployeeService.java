package com.sb11.hr_bank.domain.employee.service;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCountCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCreateRequest;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionRow;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import com.sb11.hr_bank.domain.employee.mapper.EmployeeMapper;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.domain.employee.repository.EmployeeSpecifications;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDto create(EmployeeCreateRequest dto, FileEntity file) {
        if(employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException(ErrorCode.EMPLOYEE_DUPLICATE_EMAIL);
        }

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_DEPARTMENT_NOT_FOUND));

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

        return employeeMapper.toDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        return employeeMapper.toDto(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> findAllByCondition(EmployeeSearchCondition condition) {
        return employeeRepository.findAll(EmployeeSpecifications.searchCondition(condition)).stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Long countByCondition(EmployeeCountCondition condition) {
        EmployeeCountCondition normalizedCondition = normalizeCountCondition(condition);
        return employeeRepository.count(EmployeeSpecifications.countCondition(normalizedCondition));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDistributionDto> getDistribution(EmployeeDistributionCondition condition) {
        String groupBy = condition != null && condition.groupBy() != null && !condition.groupBy().isBlank()
                ? condition.groupBy()
                : "department";

        EmployeeStatus status = condition != null && condition.status() != null
                ? condition.status()
                : EmployeeStatus.ACTIVE;

        List<EmployeeDistributionRow> rows = employeeRepository.findDistribution(groupBy, status);

        long total = rows.stream()
                .mapToLong(EmployeeDistributionRow::count)
                .sum();

        return rows.stream()
                .map(row -> new EmployeeDistributionDto(
                        row.groupKey(),
                        row.count(),
                        total == 0 ? 0.0 : Math.round(row.count() * 1000.0 / total) / 10.0
                ))
                .toList();
    }

    public void update(Long id, EmployeeUpdateRequest dto, FileEntity file) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if(!employee.getEmail().equals(dto.email())
                && employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException(ErrorCode.EMPLOYEE_DUPLICATE_EMAIL);
        }

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_DEPARTMENT_NOT_FOUND));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        employeeRepository.deleteById(id);
    }

    private EmployeeCountCondition normalizeCountCondition(EmployeeCountCondition condition) {
        if(condition == null || condition.fromDate() == null || condition.toDate() != null) {
            return condition;
        }

        return new EmployeeCountCondition(
                condition.status(),
                condition.fromDate(),
                LocalDate.now()
        );
    }
}