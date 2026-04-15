package com.sb11.hr_bank.domain.backup.service;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import java.util.List;

public interface BackupService {

  List<Backup> findAll();

}
