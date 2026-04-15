package com.sb11.hr_bank.domain.department.repository;

import com.sb11.hr_bank.domain.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}