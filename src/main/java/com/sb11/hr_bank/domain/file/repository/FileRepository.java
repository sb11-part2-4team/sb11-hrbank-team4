package com.sb11.hr_bank.domain.file.repository;

import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.entity.FileStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

  //스케줄러가 FAILED 상태인 고아 파일을 찾기 위해 사용되는 메서드
  List<FileEntity> findAllByStatus(FileStatus status);
}
