package com.sb11.hr_bank.domain.department.repository;

import com.sb11.hr_bank.domain.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // 인터페이스가 데이터 접근 계층임을 스프링에 알림
public interface DepartmentRepository extends JpaRepository<Department, Long> {
 // 상속을 통해 save(), findAll() 등 기본적인 DB 관리 기능을 얻음

  boolean existsByName(String name);
  // 부서명 중복 여부를 확인

  List<Department> findByNameContainingOrDescriptionContaining(String name, String description);
  // 부서명 또는 부서설명에 특정 단어가 포함된 부서모두 찾기
}