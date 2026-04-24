package com.sb11.hr_bank.domain.file.controller;

import com.sb11.hr_bank.domain.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 관리", description = "파일 업로드 및 다운로드 API")
public interface FileApi {

  @Operation(summary = "파일 업로드", description = "신규 파일을 서버에 업로드합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "업로드 성공"),
      @ApiResponse(responseCode = "400", description = "업로드 된 파일이 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "파일 저장 중 서버 오류 발생", content = @Content)
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<FileResponse> uploadFile(
      @Parameter(description = "업로드 대상 파일", required = true)
      @RequestParam("file")MultipartFile file
  );

  @Operation(summary = "파일 다운로드", description = "요청한 ID의 파일을 다운로드합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "다운로드 성공"),
      @ApiResponse(responseCode = "404", description = "해당 ID의 파일을 찾을 수 없음", content = @Content)
  })
  @GetMapping("/{id}/download")
  ResponseEntity<Resource> downloadFile(
      @Parameter(description = "파일 ID", example = "1")
      @PathVariable("id") Long id
  );
}
