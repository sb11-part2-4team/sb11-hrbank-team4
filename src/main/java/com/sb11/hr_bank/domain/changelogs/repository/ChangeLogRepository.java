package com.sb11.hr_bank.domain.changelogs.repository;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;


public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
  // Backup 파트에서 요청. lastBackupTime 이후 생성된 데이터가 있는지 확인하는 코드
  boolean existsByCreatedAtAfter(Instant lastBackupTime);

  // 커서 기반 페이징 쿼리
  // JPQL 예시
  @Query("""
      SELECT c FROM ChangeLog c
      WHERE c.id < :cursorId
        AND (:employeeId IS NULL OR c.employee.Id = :employeeId)
        AND (:memo IS NULL OR c.memo LIKE %:memo%)
        AND (:ipAddress IS NULL OR c.ipAddress = :ipAddress)
        AND (:type IS NULL OR c.type = :type)
        AND (CAST(:startDate AS timestamp) IS NULL OR c.createdAt >= :startDate)
        AND (CAST(:endDate AS timestamp) IS NULL OR c.createdAt <= :endDate)
      """)
    Slice<ChangeLog> findByCursorPaging(
      @Param("cursorId") Long cursorId,
      @Param("employeeId") Long employeeId,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("type") ChangeLogType type,
      @Param("startDate") Instant startDate,
      @Param("endDate") Instant endDate,
    Pageable pageable
  );

}
