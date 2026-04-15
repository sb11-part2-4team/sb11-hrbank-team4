package com.sb11.hr_bank.domain.file.controller;

import com.sb11.hr_bank.domain.file.service.FileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }
}
