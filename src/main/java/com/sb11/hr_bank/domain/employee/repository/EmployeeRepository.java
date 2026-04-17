package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee>,
        EmployeeRepositoryCustom {
    Optional<Employee> findByEmail(String email);
    Long countByHireDateBetween(LocalDate start, LocalDate end);
    Long countByHireDateLessThan(LocalDate date);
    boolean existsByDepartmentId(Long departmentId);
    List<Employee> findByDepartmentId(Long departmentId);
}