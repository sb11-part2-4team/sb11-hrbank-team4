package com.sb11.hr_bank.domain.department.service;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 이클래스가 핵심 비지니스 로직을 처리하는 서비스계층임을 선언
@RequiredArgsConstructor // final이 붙은 레포지토리를 스프링이 자동으로 연결
@Transactional(readOnly = true) // 테이터를 "읽기만" 하는 작업에서 성능 최적화


public class DepartmentService {
  private final DepartmentRepository departmentRepository;

  @Transactional // 데이터를 저장하므로 "읽기전용"을해제 트랜잭션을 적용
  public Long save(Department department) {
    // 부서 정보를 DB에 저장, 생성된 번호(ID)를 반환
    return departmentRepository.save(department).getId();
  }

  public List<Department> findAll() {
    // DB에 저장된 모든 부서 정보를 리스트 형태로 가져옴
    return departmentRepository.findAll();
  }

}
