package com.sb11.hr_bank.domain.file.controller;

import com.sb11.hr_bank.domain.file.dto.FileResponse;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.service.FileService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  //파일 업로드 API
  @PostMapping
  public ResponseEntity<FileResponse> uploadFile(
      @RequestParam("file") MultipartFile file) throws IOException {
    //파일 저장 후 ID 반환
    Long fileId = fileService.uploadFile(file);

    //저장된 파일 메타데이터 조회
    FileEntity fileEntity = fileService.getFileMetadata(fileId);

    //응답용 DTO 변환
    FileResponse response = new FileResponse(
        fileEntity.getId(),
        fileEntity.getName(),
        fileEntity.getContentType(),
        fileEntity.getSize()
    );

    //결과 반환
    return ResponseEntity.ok(response);
  }
}
