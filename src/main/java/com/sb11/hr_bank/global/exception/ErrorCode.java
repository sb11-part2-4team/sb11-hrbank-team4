package com.sb11.hr_bank.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // 에러 코드 추가할때는 도메인별로 모아서 작성
  // status 필드는 http 상태 코드
  // code 필드는 구분용. 양식은 본인 도메인 앞글자 대문자 + 숫자 세자리 (G는 global)
  // detail

  //예외 던질때 예시 -> throw new BusinessException(ErrorCode.EXAMPLE_EMPLOYEE_ERROR);



  // global
  GLOBAL_DUMMY_ERROR(500, "G001", "예시 메시지"),

  // employee
  //EXAMPLE_EMPLOYEE_ERROR(500, "E001", "예시 메시지"),

  // department
  //EXAMPLE_DEPARTMENT_ERROR(500, "D001", "예시 메시지"),

  // changeLogs
  //EXAMPLE_DEPARTMENT_ERROR(500, "C001", "예시 메시지"),

  // file
  FILE_EMPTY(400, "F001", "업로드 된 파일이 없습니다."),
  FILE_NOT_FOUND(404, "F002", "해당 ID의 파일을 찾을 수 없습니다."),
  FILE_STORAGE_ERROR(500, "F003", "파일을 저장소 통신 중 오류가 발생했습니다."),

  // backup
  //EXAMPLE_FILE_ERROR(500, "B001", "예시 메시지"),




  DUMMY_ERROR(500,"G002", "DUMMY");

  //



  
  private final int status;
  private final String code;
  private final String detail;


}
