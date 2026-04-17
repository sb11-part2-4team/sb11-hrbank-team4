package com.sb11.hr_bank.domain.changelogs.repository;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

  boolean existsByCreatedAtAfter(Instant lastBackupTime);
}
