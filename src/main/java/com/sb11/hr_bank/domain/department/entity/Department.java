package com.sb11.hr_bank.domain.department.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "departments")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Department {
  @Id // 이 핋드가 이 데이터의 고유번호임을 표기함 (주민번호역활)
  @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호를 순서대로 생성함 (1..2..3)
  private Long id;

  @Column(nullable = false, unique = true) // 공란불가, 부서명중복불가
  private String name; // 기존 departmentName을 'name' 으로 간결하게 변경

  @Column(columnDefinition = "TEXT") // 긴 글을 위해 TEXT 지정
  private String description; // 부서에 대한 상세설명

  @Column(nullable = false) // 공란불가

  private LocalDate createdDate; // 부서 '설립일'을 저장하는 날짜 변수
}
