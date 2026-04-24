package com.sb11.hr_bank.domain.changelogs.repository;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeDetailLogRepository extends JpaRepository<ChangeDetailLog,Long> {
  List<ChangeDetailLog> findByChangeLogId(Long changeLogId);
}
