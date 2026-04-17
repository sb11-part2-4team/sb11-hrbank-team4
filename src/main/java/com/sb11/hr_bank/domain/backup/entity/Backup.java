package com.sb11.hr_bank.domain.backup.entity;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.global.base.BaseEntity;
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

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor
public class Backup extends BaseEntity {

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
  @Column(name = "status", length = 20, nullable = false)
  private BackupStatus status;

  // 1개의 백업에는 1개의 파일(1:1)
  // Backup은 fileId를 참조하여 어떤 File인지 알아야함(단방향)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = true)
  private FileEntity file;

  protected Backup(String worker, BackupStatus status) {
    this.worker = worker;
    this.startedAt = Instant.now();
    this.status = status;

    // 스킵 상태일 경우 바로 종료
    if (status != BackupStatus.IN_PROGRESS) {
      this.endedAt = Instant.now();
    }
  }

  // 정적 팩토리 메서드
  public static Backup skip(String worker) {
    Backup backup = new Backup(worker, BackupStatus.SKIPPED);
    return backup;
  }

  public static Backup create(String worker) {
    return new Backup(worker, BackupStatus.IN_PROGRESS);
  }

  // IN_PROGRESS(진행중) 상태-> COMPLETED 상태로
  public void complete(FileEntity csvFile) {
    // IN_PROGRESS 상태가 아니면 예외
    if (this.status != BackupStatus.IN_PROGRESS) {
      throw new IllegalStateException("진행 중인 백업만 완료 처리할 수 있습니다.");
    }
    if (csvFile == null) {
      throw new IllegalArgumentException("백업을 완료하였지만 csvFile이 생성되지 않았습니다.");
    }
    this.file = csvFile;
    this.status = BackupStatus.COMPLETED;
    this.endedAt = Instant.now();
  }

  // IN_PROGRESS(진행중) 상태-> FAILED 상태로
  public void fail(FileEntity logFile) {
    if (this.status != BackupStatus.IN_PROGRESS) {
      throw new IllegalStateException("진행 중인 백업만 실패 처리할 수 있습니다.");
    }
    this.file = logFile;
    this.status = BackupStatus.FAILED;
    this.endedAt = Instant.now();
  }
}
// 데이터 백업 관리
// 데이터 백업한 ID, 작업자(IP주소), 백업을 시작한 시간, 백업이 완료된 시간, 상태, 파일 ID(fileId)
// 상태는 enum타입