package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String email);
    Long countByHireDateBetween(LocalDate start, LocalDate end);
    boolean existsByDepartmentId(Long departmentId);
}