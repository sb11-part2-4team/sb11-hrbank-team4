package com.sb11.hr_bank.domain.file.service;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
  private final FileRepository fileRepository;

  public FileService(FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  public Long uploadFile(MultipartFile file) {
    return null;
  }

  public FileEntity getFileMetadata(Long id) {
    return null;
  }
}
