package com.sb11.hr_bank.domain.file.storage;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface FileStorage {
  ResponseEntity<Resource> download(FileEntity fileEntity);
}
