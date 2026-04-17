package com.sb11.hr_bank.domain.file.storage;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

@Component
public class LocalFileStorage implements FileStorage {
  private final Path rootPath = Paths.get("uploads");

  @Override
  public ResponseEntity<Resource> download(FileEntity fileEntity) {

    try {
      //저장된 로컬 경로를 찾아 반환
      Path filePath = rootPath.resolve(fileEntity.getId().toString());
      Resource resource = new UrlResource(filePath.toUri());

      //파일이 존재하는지, 읽을 수 있는지 확인
      if (!resource.exists() || !resource.isReadable()) {
        throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
      }

      //한글 파일명 깨짐 방지 인코딩
      String encodedFilename = UriUtils.encode(fileEntity.getName(), StandardCharsets.UTF_8);

      //브라우저가 다운로드 창을 띄우도록 헤더 설정
      String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"";

      //파일 데이터와 헤더 합쳐서 반환
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(fileEntity.getContentType()))
          .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
          .body(resource);
    } catch (MalformedURLException e) {
      throw new RuntimeException("파일 경로가 잘못되었습니다.", e);
    }
  }
}
