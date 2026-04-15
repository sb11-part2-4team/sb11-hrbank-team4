package com.sb11.hr_bank.domain.file.service;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class FileService {
  private final FileRepository fileRepository;

  //파일 저장 경로(./uploads)
  @Value("${file.upload-dir:./uploads}")
  private String uploadDir;

  public FileService(FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  //파일 업로드 클래스 (로컬 디스크 저장 및 DB 기록)
  @Transactional
  public Long uploadFile(MultipartFile file) throws IOException {
    //빈 파일 검증
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("업로드 된 파일이 없습니다.");
    }

    //저장할 폴더가 없을 시 생성
    File directory = new File(uploadDir);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    //파일명 중복 방지를 위해 고유한 이름 생성(UUID 활용)
    String originalFilename = file.getOriginalFilename();
    String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
    String savedPath = Paths.get(uploadDir, savedFilename).toAbsolutePath().toString();

    //파일을 로컬 디스크에 저장
    File destFile = new File(savedPath);
    file.transferTo(destFile);

    //DB 메타데이터 저장
    FileEntity fileEntity = new FileEntity(
        originalFilename,
        file.getContentType(),
        file.getSize(),
        savedPath
    );

    //DB에 정보 저장 후, 저장된 결과물 반환
    FileEntity savedEntity = fileRepository.save(fileEntity);

    //저장된 파일의 ID 리턴
    return savedEntity.getId();
  }

  //파일 메타데이터 단건 조회
  public FileEntity getFileMetadata(Long id) {
    return fileRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 ID의 파일을 찾을 수 없습니다.: " + id));
  }
}
