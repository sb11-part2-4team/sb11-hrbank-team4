package com.sb11.hr_bank.backup.entity;

import com.sb11.hr_bank.entity.FileEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "backups")
@Getter @Setter
@NoArgsConstructor
public class Backup { // extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
  private Long id;

  @Column(name = "worker", length = 20, nullable = false)
  private String worker;

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  @Column(name = "ended_at", nullable = true)
  private Instant endedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private BackupStatus status;

  // 1개의 백업에는 1개의 파일(1:1)
  // Backup은 fileId를 참조하여 어떤 File인지 알아야함(단방향)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = true)
  private FileEntity fileId;

  private Backup(String worker, Instant startedAt, Instant endedAt, BackupStatus status, FileEntity fileId) {
    this.worker = worker;
    this.startedAt = Instant.now();
    this.endedAt = Instant.now();
    this.status = status;
    this.fileId = fileId;
  }
}
// 데이터 백업 관리
// 데이터 백업한 ID, 작업자(IP주소), 백업을 시작한 시간, 백업이 완료된 시간, 상태, 파일 ID(fileId)
// ID는 추후에 BaseEntity ?
// 상태는 enum타입