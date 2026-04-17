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
  EMPLOYEE_NOT_FOUND(404, "E001", "직원을 찾을 수 없습니다."),
  EMPLOYEE_DUPLICATE_EMAIL(409, "E002", "이미 사용 중인 이메일입니다."),
  EMPLOYEE_DEPARTMENT_NOT_FOUND(404, "E003", "부서를 찾을 수 없습니다."),

  // department
  //EXAMPLE_DEPARTMENT_ERROR(500, "D001", "예시 메시지"),

  // changeLogs
  //EXAMPLE_DEPARTMENT_ERROR(500, "C001", "예시 메시지"),

  // file
  //EXAMPLE_FILE_ERROR(500, "F001", "예시 메시지"),

  // backup
  //EXAMPLE_FILE_ERROR(500, "B001", "예시 메시지"),




  DUMMY_ERROR(500,"G002", "DUMMY");

  //



  
  private final int status;
  private final String code;
  private final String detail;


}
