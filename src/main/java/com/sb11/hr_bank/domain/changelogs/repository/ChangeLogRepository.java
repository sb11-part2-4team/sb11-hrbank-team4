package com.sb11.hr_bank.domain.changelogs.repository;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
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
  // 첫 페이지용 페이징 쿼리
  @Query("SELECT c FROM ChangeLog c ORDER BY c.id DESC")
  Slice<ChangeLog> findFirstPage(Pageable pageable);

  // Where c.id < :lastId(커서 역할. 마지막으로 본 데이터의 ID보다 과거의 데이터 가져오는 필터링)
  // ORDER BY c.id DESC (최근 데이터부터 보여주기 위해 내림차순으로 정렬함.)
  @Query("SELECT c FROM ChangeLog c Where c.id < :lastId ORDER BY c.id DESC")
  // @Param("lastId") : 메서드 파라미터로 들어온 `lastId`값을 쿼리문의 `:lastId`자리에 넣어줌
  Slice<ChangeLog> findByCursorPaging(@Param("lastId") Long lastId, Pageable pageable);

}
