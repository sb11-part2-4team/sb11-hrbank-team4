package com.sb11.hr_bank.domain.backup.repository;


import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BackupRepository extends JpaRepository<Backup, Long> {
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status); // EndedAt 내림차순 정렬 가장 위 status를 확인

  @Query("SELECT MAX(endedAt) FROM Backup WHERE status = 'COMPLETED'")
  Optional<Instant> findLastCompletedTime();
}
