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

  // Employee Entity 연결
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

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
  public ChangeLog(Employee employee, ChangeLogType type, String memo, String ipAddress) {
    this.employee = employee;
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
