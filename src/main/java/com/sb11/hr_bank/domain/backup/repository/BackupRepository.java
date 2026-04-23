package com.sb11.hr_bank.domain.backup.repository;


import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long>, BackupRepositoryCustom {

  // EndedAt 내림차순 정렬 가장 위 status를 확인
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status);

  // 백업 상태 체크
  boolean existsByStatus(BackupStatus status);
}
