package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.employee.entity.Employee;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository
    extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor<Employee>,
    EmployeeRepositoryCustom {

  Optional<Employee> findByEmail(String email);

  Long countByHireDateBetween(LocalDate start, LocalDate end);

  Long countByHireDateLessThan(LocalDate date);

  boolean existsByDepartmentId(Long departmentId);

  List<Employee> findByDepartmentId(Long departmentId);

  List<Employee> findByDepartmentIdIn(List<Long> departmentIds);

  @Query("SELECT MAX(e.employeeNumber) FROM Employee e WHERE e.employeeNumber LIKE CONCAT('EMP-', :year, '-%')")
  Optional<String> findMaxEmployeeNumberByYear(@Param("year") int year);

  @Query("SELECT e FROM Employee e JOIN FETCH e.department")
  List<Employee> findAllWithDepartment();

  boolean existsByIdIsNotNull();
}