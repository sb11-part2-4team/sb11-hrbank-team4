package com.sb11.hr_bank.domain.changelogs.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "change_detail_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeDetailLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ChangeLog 와 다대일 연결
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "change_log_id", nullable = false)
  private ChangeLog changeLog;

  // 달라진 항목
  @Column(name = "column_name", nullable = false, length = 50)
  private String columnName;

  // 변경 전 데이터
  @Column(name = "before_value", columnDefinition = "TEXT", nullable = false)
  private String beforeValue;

  // 변경 후 데이터
  @Column(name = "after_value", columnDefinition = "TEXT",  nullable = false)
  private String afterValue;

  @Builder
  public ChangeDetailLog(String columnName, String beforeValue, String afterValue) {
    this.columnName = columnName;
    // 신규 직원 등록시 이전 값 없으므로 - 로 채움
    this.beforeValue = (beforeValue == null || beforeValue.isBlank()) ? "-" : beforeValue;
    // 기존 직원 삭제 시 이후 값 없으므로 - 로 채움
    this.afterValue = (afterValue == null || afterValue.isBlank()) ? "-" : afterValue;
  }

  // ChangeLoge의 addDetail에서 호출(연관관계 편의 메서드)
  public void assignChangeLog(ChangeLog changeLog) {
    this.changeLog = changeLog;
  }

}
