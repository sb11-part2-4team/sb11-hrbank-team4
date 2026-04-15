package com.sb11.hr_bank.domain.backup.controller;

import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.service.BackupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 데이터 백업 관리 API
// 데이터 백업 목록 조회(GET, api/backups)
// 데이터 백업 생성(POST, api/backups)
// 최근 백업 정보 조회(GET, api/backpus/latest)
@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

}
