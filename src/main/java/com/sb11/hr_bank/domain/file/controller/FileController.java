package com.sb11.hr_bank.domain.file.controller;

import com.sb11.hr_bank.domain.file.dto.FileResponse;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.service.FileService;
import com.sb11.hr_bank.domain.file.storage.FileStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;
  private final FileStorage fileStorage;

  //파일 업로드 API
  @PostMapping
  public ResponseEntity<FileResponse> uploadFile(
      @RequestParam("file") MultipartFile file) {

    //파일 저장 후 엔티티 반환
    FileEntity fileEntity = fileService.uploadFile(file);

    //응답용 DTO 변환
    FileResponse response = new FileResponse(
        fileEntity.getId(),
        fileEntity.getName(),
        fileEntity.getContentType(),
        fileEntity.getSize()
    );

    //실행 결과 반환
    return ResponseEntity.ok(response);
  }

  //파일 다운로드
  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> downloadFile(
      @PathVariable("id") Long id) {
    //파일 정보 조회
    FileEntity fileEntity = fileService.getFileMetadata(id);

    return fileStorage.download(fileEntity);
  }

  //파일 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(
      @PathVariable("id") Long id) {
    fileService.deleteFile(id);
    return ResponseEntity.noContent().build();
  }
}
