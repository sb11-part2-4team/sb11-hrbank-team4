package com.sb11.hr_bank.domain.backup.repository;


import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackupRepository extends JpaRepository<Backup, Long>, BackupRepositoryCustom {

  // EndedAt 내림차순 정렬 가장 위 status를 확인
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status);

  // 백업 상태 체크(동시성 문제는 Lock으로 제어)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select b from Backup b where b.status = :status")
  Optional<Backup> findByStatus(@Param("status") BackupStatus status);
}
