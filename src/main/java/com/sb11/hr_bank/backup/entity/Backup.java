package com.sb11.hr_bank.backup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.Getter;

@Entity
@Getter
public class Backup extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
  private Long id;

  @Column(name = "worker", length = 20, nullable = false)
  private String worker;

  @Column(name = "startedAt", nullable = false)
  private Instant startedAt;

  @Column(name = "endedAt", nullable = false)
  private Instant endedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private Status status;

  @Column(name = "fileId")
  private Long fileId;

  public enum Status {
    IN_PROGRESS("진행중"), COMPLETED("완료"), FAILED("실패"), SKIPPED("건너뜀");

    private final String description;

    Status(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }
}
// 데이터 백업 관리
// 데이터 백업한 ID, 작업자(IP주소), 백업을 시작한 시간, 백업이 완료된 시간, 상태, 파일 ID(fileId)
// ID는 BaseEntity
// 상태는 enum타입(IN_PROGRESS(진행중), COMPLETED(완료), FAILED(실패))