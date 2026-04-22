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

  // 1. Backup 파트용: 특정 시간 이후 데이터 존재 여부
  boolean existsByCreatedAtAfter(Instant lastBackupTime);

  // 2. 통계/카운트용: 특정 기간 내 데이터 개수
  long countByCreatedAtBetween(Instant fromDate, Instant toDate);

  // 3. [시간(at) 정렬용] 복합 커서 페이징
  @Query("""
        SELECT c FROM ChangeLog c
        JOIN FETCH c.employee e
        WHERE (
            (:atAfter IS NULL OR c.createdAt < :atAfter) 
            OR (c.createdAt = :atAfter AND c.id < :idAfter)
        )
        AND (:empNum IS NULL OR e.employeeNumber = :empNum)
        AND (:memo IS NULL OR c.memo LIKE CONCAT('%', :memo, '%'))
        AND (:searchIp IS NULL OR c.ipAddress = :searchIp)
        AND (:type IS NULL OR c.type = :type)
        AND (CAST(:atFrom AS timestamp) IS NULL OR c.createdAt >= :atFrom)
        AND (CAST(:atTo AS timestamp) IS NULL OR c.createdAt <= :atTo)
        ORDER BY c.createdAt DESC, c.id DESC
        """)
  Slice<ChangeLog> findByCursorAtDesc(
      @Param("atAfter") Instant atAfter,
      @Param("idAfter") Long idAfter,
      @Param("empNum") String empNum,
      @Param("memo") String memo,
      @Param("searchIp") String searchIp,
      @Param("type") ChangeLogType type,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      Pageable pageable
  );

  // 4. [IP 정렬용] 복합 커서 페이징
  @Query("""
        SELECT c FROM ChangeLog c
        JOIN FETCH c.employee e
        WHERE (
            (:ipAfter IS NULL OR c.ipAddress < :ipAfter) 
            OR (c.ipAddress = :ipAfter AND c.id < :idAfter)
        )
        AND (:empNum IS NULL OR e.employeeNumber = :empNum)
        AND (:memo IS NULL OR c.memo LIKE CONCAT('%', :memo, '%'))
        AND (:searchIp IS NULL OR c.ipAddress = :searchIp)
        AND (:type IS NULL OR c.type = :type)
        AND (CAST(:atFrom AS timestamp) IS NULL OR c.createdAt >= :atFrom)
        AND (CAST(:atTo AS timestamp) IS NULL OR c.createdAt <= :atTo)
        ORDER BY c.ipAddress DESC, c.id DESC
        """)
  Slice<ChangeLog> findByCursorIpDesc(
      @Param("ipAfter") String ipAfter,
      @Param("idAfter") Long idAfter,
      @Param("empNum") String empNum,
      @Param("memo") String memo,
      @Param("searchIp") String searchIp,
      @Param("type") ChangeLogType type,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      Pageable pageable
  );
}