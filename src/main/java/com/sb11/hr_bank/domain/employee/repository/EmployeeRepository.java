package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Long countByHireDateBetween(LocalDate start, LocalDate end);
}