package com.sb11.hr_bank.domain.backup.mapper;

import com.sb11.hr_bank.domain.backup.dto.BackupResponse;
import com.sb11.hr_bank.domain.backup.entity.Backup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackupMapper {

  private final FileMapper fileMapper;

  public BackupResponse from(Backup backup) {
    return new BackupResponse(
        backup.getId(),
        backup.getWorker(),
        backup.getStartedAt(),
        backup.getEndedAt(),
        backup.getStatus(),
        backup.getFile() != null ? fileMapper.from(backup.getFile())
            : null// 정적 팩토리 메서드로 작성할 시 교체 예정
    );
  }

}

//@Component
//@RequiredArgsConstructor
//public class FileMapper {
//
//  // ...
//  public FileResponse from(FileEntity file) {
//    return new FileResponse(
//        file.getId(),
//        file.getName(),
//        file.getContentType(),
//        file.getSize()
//    );
//  }
//}