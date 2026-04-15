package com.sb11.hr_bank.domain.changelogs.entity;

import com.sb11.hr_bank.employee.Employee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "change_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Employee Entity 연결
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ChangeLogType type;

  @Column(columnDefinition = "TEXT")
  private String memo;

  @Column(name = "ip_address", nullable = false, length = 20)
  private String ipAddress;

  @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChangeDateilLog> details = new ArrayList<>();

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
