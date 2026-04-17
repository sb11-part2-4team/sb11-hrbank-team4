package com.sb11.hr_bank.domain.changelogs.service;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeDetailLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;


}
