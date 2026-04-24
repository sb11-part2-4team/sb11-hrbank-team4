package com.sb11.hr_bank.domain.file.entity;

public enum FileStatus {
  PENDING,  //파일 업로드 시작
  ACTIVE,   //파일 저장 완료 및 정상 사용 가능 상태
  FAILED    //파일 저장 중 오류 발생
}
