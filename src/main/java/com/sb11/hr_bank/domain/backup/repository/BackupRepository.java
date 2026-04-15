package com.sb11.hr_bank.backup.repository;

import com.sb11.hr_bank.backup.entity.Backup;
import com.sb11.hr_bank.backup.entity.BackupStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long> {
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status); // EndedAt 내림차순 정렬 가장 위 status를 확인

}
