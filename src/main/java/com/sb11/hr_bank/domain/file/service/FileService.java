package com.sb11.hr_bank.domain.file.service;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.entity.FileStatus;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
  @Transactional(noRollbackFor = BusinessException.class)
  public FileEntity uploadFile(MultipartFile file) {
    //빈 파일 검증
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ErrorCode.FILE_EMPTY);
    }

    //PENDING 상태로 DB 메타데이터 저장
    FileEntity savedEntity = saveMetadata(file.getOriginalFilename(), file.getContentType(), file.getSize());

    //ID를 파일명으로 로컬 디스크에 저장
    Path destPath = getAbsolutePath(savedEntity.getId());

    try {
      //로컬 파일 저장 시도
      file.transferTo(destPath.toFile());

      //파일 저장 성공 시 상태를 ACTIVE로 변경
      savedEntity.activate();
      return fileRepository.save(savedEntity);

    } catch (IOException | IllegalStateException e) {
      log.error("파일 업로드 실패. 파일 ID: {}", savedEntity.getId(), e);
      //실패 시 상태를 FAILED로 변경
      savedEntity.fail();
      fileRepository.save(savedEntity);
      throw new BusinessException(ErrorCode.FILE_STORAGE_ERROR);
    }
  }

  //파일 메타데이터 단건 조회(ACTIVE 상태인 파일만 조회 가능)
  public FileEntity getFileMetadata(Long id) {
    FileEntity entity = fileRepository.findById(id).orElseThrow(
        () -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

    if (entity.getStatus() != FileStatus.ACTIVE) {
      throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
    }

    return entity;
  }

  //서버 내부 파일 저장용 메서드
  @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BusinessException.class)
  public FileEntity saveInternalData(String originalFilename, String contentType, byte[] data) {
    //data 검증
    if (data == null || data.length == 0) {
      throw new BusinessException(ErrorCode.FILE_EMPTY);
    }

    FileEntity savedEntity = saveMetadata(originalFilename, contentType, (long) data.length);
    //ID를 파일명으로 로컬 디스크에 기록
    Path destPath = getAbsolutePath(savedEntity.getId());
    
    try {
      //파일 기록 시도
      Files.write(destPath, data);

      //성공 시 ACTIVE
      savedEntity.activate();
      return fileRepository.save(savedEntity);

    } catch (IOException e) {
      log.error("내부 파일 저장 실패. 파일 ID: {}", savedEntity.getId(), e);
      //실패 시 FAILED
      savedEntity.fail();
      fileRepository.save(savedEntity);
      throw new BusinessException(ErrorCode.FILE_STORAGE_ERROR);
    }
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
    Path destPath = getAbsolutePath(fileEntity.getId());

    //DB 메타데이터 삭제
    fileRepository.delete(fileEntity);

    //DB 트랙잭션이 커밋된 이후 로컬 파일 삭제
    try {
      Files.deleteIfExists(destPath);
      log.info("로컬 파일 삭제 성공: {}", destPath);
    } catch (IOException e) {
      //DB 커밋이 끝났으므로 롤백 불가, 에러 로그 남김
      log.error("로컬 파일 삭제 실패: {}", destPath, e);
    }
  }

  @Transactional
  public void deleteFailedFile(FileEntity fileEntity) {
    Path destPath = getAbsolutePath(fileEntity.getId());

    //DB 메타데이터 강제 삭제
    fileRepository.delete(fileEntity);

    try {
      Files.deleteIfExists(destPath);
      log.info("로컬 더미 파일 삭제 완료: {}", destPath);
    } catch (IOException e) {
      log.error("로컬 더미 파일 삭제 실패: {}", destPath, e);
    }
  }
}
