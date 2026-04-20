package com.sb11.hr_bank.domain.file.service;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Slf4j //로그 사용을 위한 어노테이션
@Service
@Transactional(readOnly = true)
public class FileService {
  private final FileRepository fileRepository;

  private final Path rootPath = Paths.get("uploads");

  public FileService(FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  //파일 업로드 클래스 (로컬 디스크 저장 및 DB 기록)
  @Transactional
  public FileEntity uploadFile(MultipartFile file) {
    //빈 파일 검증
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ErrorCode.FILE_EMPTY);
    }

    FileEntity savedEntity = saveMetadata(file.getOriginalFilename(), file.getContentType(), file.getSize());

    //ID를 파일명으로 로컬 디스크에 저장
    Path destPath = getAbsolutePath(savedEntity.getId());

    try {
      file.transferTo(destPath.toFile());
    } catch (IOException | IllegalStateException e) {
      throw new BusinessException(ErrorCode.FILE_STORAGE_ERROR);
    }

    //저장된 파일의 ID 리턴
    return savedEntity;
  }

  //파일 메타데이터 단건 조회
  public FileEntity getFileMetadata(Long id) {
    return fileRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ErrorCode.FILE_NOT_FOUND));
  }

  //서버 내부 파일 저장용 메서드
  @Transactional
  public FileEntity saveInternalData(String originalFilename, String contentType, byte[] data) {
    //data 검증
    if (data == null || data.length == 0) {
      throw new BusinessException(ErrorCode.FILE_EMPTY);
    }

    FileEntity savedEntity = saveMetadata(originalFilename, contentType, (long) data.length);
    //ID를 파일명으로 로컬 디스크에 기록
    Path destPath = getAbsolutePath(savedEntity.getId());
    try {
      Files.write(destPath, data);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.FILE_STORAGE_ERROR);
    }
    return savedEntity;
  }

  //파일 저장 공용 메서드
  private FileEntity saveMetadata(String name, String contentType, Long size) {
    //저장할 폴더가 없을 시 생성
    File directory = rootPath.toFile();
    if (!directory.exists()) {
      directory.mkdirs();
    }

    //DB 메타데이터 저장
    FileEntity fileEntity = new FileEntity(name, contentType, size);

    //DB에 저장되며 ID 발급
    return fileRepository.save(fileEntity);
  }

  private Path getAbsolutePath(Long id) {
    return rootPath.resolve(id.toString()).toAbsolutePath();
  }

  //파일 삭제 메서드
  @Transactional
  public void deleteFile(Long id) {
    //DB 메타데이터 조회
    FileEntity fileEntity = getFileMetadata(id);

    //로컬 디스크 파일 삭제
    Path destPath = getAbsolutePath(fileEntity.getId());

    //DB 메타데이터 삭제
    fileRepository.delete(fileEntity);

    //DB 트랙잭션이 커밋된 이후 로컬 파일 삭제
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        try {
          Files.deleteIfExists(destPath);
          log.info("로컬 파일 삭제 성공: {}", destPath);
        } catch (IOException e) {
          //DB 커밋이 끝났으므로 롤백 불가, 에러 로그 남김
          log.error("로컬 파일 삭제 실패: {}", destPath, e);
        }
      }
    });
  }
}
