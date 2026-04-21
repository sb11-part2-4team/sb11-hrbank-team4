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

  // count API용
  long countByCreatedAtBetween(Instant fromDate, Instant toDate);

  // 커서 기반 페이징 쿼리
  // DESC 정렬, JOIN FETCH로 Employee N+1 방지
  @Query("""
      SELECT c FROM ChangeLog c
      JOIN FETCH c.employee e
      WHERE c.id < :idAfter
        AND (:employeeNumber IS NULL OR e.employeeNumber = :employeeNumber)
        AND (:memo IS NULL OR c.memo LIKE CONCAT('%', :memo, '%'))
        AND (:ipAddress IS NULL OR c.ipAddress = :ipAddress)
        AND (:type IS NULL OR c.type = :type)
        AND (CAST(:atFrom AS timestamp) IS NULL OR c.createdAt >= :atFrom)
        AND (CAST(:atTo AS timestamp) IS NULL OR c.createdAt <= :atTo)
      """)
  Slice<ChangeLog> findByCursorPagingDesc(
      @Param("idAfter") Long idAfter,
      @Param("employeeNumber") String employeeNumber,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("type") ChangeLogType type,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      Pageable pageable
  );

  // ASC 정렬, JOIN FETCH로 Employee N+1 방지
  @Query("""
        SELECT c FROM ChangeLog c
        JOIN FETCH c.employee e
        WHERE c.id > :idAfter
          AND (:employeeNumber IS NULL OR e.employeeNumber = :employeeNumber)
          AND (:memo IS NULL OR c.memo LIKE CONCAT('%', :memo, '%'))
          AND (:ipAddress IS NULL OR c.ipAddress = :ipAddress)
          AND (:type IS NULL OR c.type = :type)
          AND (CAST(:atFrom AS timestamp) IS NULL OR c.createdAt >= :atFrom)
          AND (CAST(:atTo AS timestamp) IS NULL OR c.createdAt <= :atTo)
        """)
  Slice<ChangeLog> findByCursorPagingAsc(
      @Param("idAfter") Long idAfter,
      @Param("employeeNumber") String employeeNumber,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("type") ChangeLogType type,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      Pageable pageable
  );

}
