package com.sb11.hr_bank.domain.file.service;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
      throw new IllegalArgumentException("업로드 된 파일이 없습니다.");
    }

    //저장할 폴더가 없을 시 생성
    File directory = rootPath.toFile();
    if (!directory.exists()) {
      directory.mkdirs();
    }

    //DB 메타데이터 저장
    FileEntity fileEntity = new FileEntity(
        file.getOriginalFilename(),
        file.getContentType(),
        file.getSize()
    );

    //DB에 저장되며 ID 발급
    FileEntity savedEntity = fileRepository.save(fileEntity);

    //ID를 파일명으로 로컬 디스크에 저장
    Path destPath = rootPath.resolve(savedEntity.getId().toString()).toAbsolutePath();


    try {
      file.transferTo(destPath.toFile());
    } catch (IOException e) {
      throw new RuntimeException("로컬 파일 저장에 실패하여 DB 기록을 취소합니다.", e);
    }

    //저장된 파일의 ID 리턴
    return savedEntity;
  }

  //파일 메타데이터 단건 조회
  public FileEntity getFileMetadata(Long id) {
    return fileRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 ID의 파일을 찾을 수 없습니다.: " + id));
  }
}
