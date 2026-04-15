package com.sb11.hr_bank.domain.department.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // Department Class를 부서 정보를 담는 DB테이블로 사용
@Table(name = "department") // 실제로 DB에 만들어질 테이블 이름을 소문자로 지정
@Getter @Setter // 변수 값을 읽거나 저장하는 메서드를 자동으로 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 객체를 생성할떄 필요한 기본생성자를 생성함
@AllArgsConstructor // 모든 필드값에 한꺼번에 넣어 객체를 만드는 생성자를 생성함
@Builder // 필드 이름을 보면서 안전하게 값을 넣는 빌더패턴을 적용함

public class Department {
  @Id // 이 핋드가 이 데이터의 고유번호임을 표기함 (주민번호역활)
  @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호를 순서대로 생성함 (1..2..3)
  private Long id;

  @Column(nullable = false) // DB 저장시 공란을 허용 안함
  private String departmentName; // 부서명

  @Column(nullable = false) // DB 저장시 공란을 허용 안함
  private String managerName;    // 부서장명
}