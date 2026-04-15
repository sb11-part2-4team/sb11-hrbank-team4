package com.sb11.hr_bank.domain.department.repository;

import com.sb11.hr_bank.domain.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 인터페이스가 데이터 접근 계층임을 스프링에 알림
public interface DepartmentRepository extends JpaRepository<Department, Long> {
 // 상속을 통해 save(), findAll() 등 기본적인 DB 관리 기능을 얻음
}