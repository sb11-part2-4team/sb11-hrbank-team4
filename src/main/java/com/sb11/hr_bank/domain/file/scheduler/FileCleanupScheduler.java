package com.sb11.hr_bank.domain.file.scheduler;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.entity.FileStatus;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import com.sb11.hr_bank.domain.file.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

  private final FileRepository fileRepository;
  private final FileService fileService;

  @Scheduled(cron = "0 0 3 * * *")
  public void cleanupFailedFiles() {
    log.info("고아 파일 정리 스케줄러 시작");

    //FAILED 상태인 파일 조회
    List<FileEntity> failedFiles = fileRepository.findAllByStatus(FileStatus.FAILED);

    for (FileEntity file : failedFiles) {
      try {
        fileService.deleteFailedFile(file);
      } catch (Exception e) {
        log.error("실패 파일 삭제 중 오류 발생: ID = {}", file.getId(), e);
      }
    }

    log.info("고아 파일 정리 스케줄러 종료. 총 {}건 처리", failedFiles.size());
  }
}
