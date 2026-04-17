package com.sb11.hr_bank.domain.changelogs.repository;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
  // Backup 파트에서 요청. 특정 Instant 이후 데이터가 있는지 확인하는 코드
  boolean existsByCreatedAtAfter(Instant lastBackupTime);

}
