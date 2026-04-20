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
import com.sb11.hr_bank.domain.employee.dto.EmployeeTrendCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeTrendDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import com.sb11.hr_bank.domain.employee.mapper.EmployeeMapper;
import com.sb11.hr_bank.domain.employee.mapper.EmployeePageResponseMapper;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.domain.employee.repository.EmployeeSpecifications;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeePageResponseMapper employeePageResponseMapper;

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
    public PageResponse<EmployeeDto> findAllByCondition(EmployeeSearchCondition condition) {
        int size = pageSize(condition);

        Page<Employee> page = employeeRepository.findAll(
                EmployeeSpecifications.searchCondition(condition),
                PageRequest.of(0, size + 1)
        );
        Long totalElements = employeeRepository.count(EmployeeSpecifications.searchCondition(condition));
        boolean hasNext = page.getContent().size() > size;

        return employeePageResponseMapper.toPageResponse(
                page.getContent(),
                size,
                totalElements,
                hasNext
        );
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
                        calculateRate(row.count(), total)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition) {
        String unit = condition != null && condition.unit() != null && !condition.unit().isBlank()
                ? condition.unit()
                : "month";

        if(!List.of("day", "week", "month", "quarter", "year").contains(unit)) {
            throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
        }

        LocalDate to = condition != null && condition.to() != null
                ? condition.to()
                : LocalDate.now();

        LocalDate from = condition != null && condition.from() != null
                ? condition.from()
                : switch (unit) {
                    case "day" -> to.minusDays(11);
                    case "week" -> to.minusWeeks(11);
                    case "month" -> to.minusMonths(11);
                    case "quarter" -> to.minusMonths(33);
                    case "year" -> to.minusYears(11);
                    default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
                };

        if(from.isAfter(to)) {
            throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_DATE_RANGE);
        }

        List<LocalDate> buckets = new ArrayList<>();
        LocalDate current = from;

        while(!current.isAfter(to)) {
            buckets.add(current);
            current = nextDate(current, unit);
        }

        List<EmployeeTrendDto> result = new ArrayList<>();
        long previousCount = employeeRepository.countByHireDateLessThan(from);
        long count = previousCount;

        for(LocalDate bucket : buckets) {
            LocalDate bucketEnd = nextDate(bucket, unit).minusDays(1);
            if(bucketEnd.isAfter(to)) {
                bucketEnd = to;
            }

            long change = employeeRepository.countByHireDateBetween(bucket, bucketEnd);
            count += change;

            result.add(new EmployeeTrendDto(
                    bucket.toString(),
                    count,
                    change,
                    calculateRate(change, previousCount)
            ));

            previousCount = count;
        }

        return result;
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

        employeeRepository.delete(employee);
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

    private double calculateRate(long value, long total) {
        if(total == 0) {
            return 0.0;
        }

        return Math.round(value * 1000.0 / total) / 10.0;
    }

    private LocalDate nextDate(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date.plusDays(1);
            case "week" -> date.plusWeeks(1);
            case "month" -> date.plusMonths(1);
            case "quarter" -> date.plusMonths(3);
            case "year" -> date.plusYears(1);
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
        };
    }

    private int pageSize(EmployeeSearchCondition condition) {
        if(condition == null || condition.size() == null || condition.size() <= 0) {
            return 10;
        }

        return condition.size();
    }
}
