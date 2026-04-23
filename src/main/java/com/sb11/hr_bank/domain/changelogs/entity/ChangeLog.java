package com.sb11.hr_bank.domain.changelogs.entity;

import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "change_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 삭제 시 Employee 엔티티 참조 충돌을 피하기 위해 ID만 보관
  @Column(name = "employee_id")
  private Long employeeId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private ChangeLogType type;

  @Column(name = "memo", columnDefinition = "TEXT")
  private String memo;

  @Column(name = "ip_address", nullable = false, length = 20)
  private String ipAddress;

  @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChangeDetailLog> details = new ArrayList<>();

  @Builder
  public ChangeLog(Long employeeId, ChangeLogType type, String memo, String ipAddress) {
    this.employeeId = employeeId;
    this.type = type;
    this.memo = memo;
    this.ipAddress = ipAddress;
  }

  // 상세 이력 추가 시 양방향 매핑 자동 처리
  public void addDetail(ChangeDetailLog detail) {
    this.details.add(detail);
    detail.assignChangeLog(this);
  }
}
