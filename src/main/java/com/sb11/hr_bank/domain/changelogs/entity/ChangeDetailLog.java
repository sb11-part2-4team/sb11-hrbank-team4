package com.sb11.hr_bank.domain.changelogs.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "change_detail_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeDetailLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ChangeLog과의 다대일 연결
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "change_log_id", nullable = false)
  private ChangeLog changeLog;

  // 어떤 항목이 변경되었는지
  @Column(name = "column_name", nullable = false, length = 50)
  private String columnName;

  // 변경 전 데이터
  @Column(name = "before", columnDefinition = "TEXT")
  private String before;

  // 변경 후 데이터
  @Column(name = "after", columnDefinition = "TEXT")
  private String after;

  @Builder
  public ChangeDetailLog(String columnName, String before, String after) {
    this.columnName = columnName;
    this.before = before;
    this.after = after;

  }

  // 연관관계 편의 메서드 (ChangeLog의 addDetail에서 호출됨)
  public void assignChangeLog(ChangeLog changeLog) {
    this.changeLog = changeLog;
  }
}

