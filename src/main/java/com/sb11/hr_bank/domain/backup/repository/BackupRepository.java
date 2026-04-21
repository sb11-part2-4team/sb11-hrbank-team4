package com.sb11.hr_bank.domain.backup.repository;


import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackupRepository extends JpaRepository<Backup, Long> {

  // EndedAt 내림차순 정렬 가장 위 status를 확인
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status);

  @Query("""
      Select b from Backup b
            where
            (:worker is null or b.worker like %:worker%)
            and(:status is null or b.status = :status)
            and(:startFrom is null or b.startedAt >= :startFrom)
            and(:startTo is null or b.startedAt <= :startTo)
            and(
                :cursorStartedAt is null
                or b.startedAt < :cursorStartedAt
                or (b.startedAt = :cursorStartedAt and b.id < :cursorId)
            )
            order by b.startedAt desc, b.id desc
      """)
  Slice<Backup> search(@Param("worker") String worker,
      @Param("status") BackupStatus status,
      @Param("startFrom") Instant startFrom, @Param("startTo") Instant startTo,
      @Param("cursorStartedAt") Instant cursorStartedAt, @Param("cursorId") Long cursorId,
      Pageable pageable);
}
