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
  EMPLOYEE_INVALID_GROUP_BY(400, "E004", "groupBy는 department 또는 position만 가능합니다."),
  EMPLOYEE_INVALID_TREND_UNIT(400, "E005", "unit은 day, week, month, quarter, year만 가능합니다."),
  EMPLOYEE_INVALID_DATE_RANGE(400, "E006", "from은 to보다 이후일 수 없습니다."),
  EMPLOYEE_INVALID_SORT_DIRECTION(400, "E007", "sortDirection은 asc 또는 desc만 가능합니다."),
  EMPLOYEE_INVALID_SORT_FIELD(400, "E008", "sortField는 name, employeeNumber, hireDate만 가능합니다."),
  EMPLOYEE_CURSOR_ENCODING_FAILED(500, "E009", "직원 목록 커서 생성에 실패했습니다."),
  EMPLOYEE_INVALID_CURSOR(400, "E010", "유효하지 않은 직원 목록 커서입니다."),

  // department
  //EXAMPLE_DEPARTMENT_ERROR(500, "D001", "예시 메시지"),
  DEPARTMENT_NOT_FOUND(404, "D001", "해당 부서를 찾을 수 없습니다."),
  DEPARTMENT_DUPLICATE_NAME(409, "D002", "이미 존재하는 부서명입니다."),
  DEPARTMENT_HAS_EMPLOYEES(409, "D003", "해당 부서에 소속된 직원이 있어 삭제할 수 없습니다."),

  // changeLogs
  //EXAMPLE_DEPARTMENT_ERROR(500, "C001", "예시 메시지"),
  CHANGELOG_NOT_FOUND(404, "C001", "해당 이력을 찾을 수 없습니다."),

  // file
  FILE_EMPTY(400, "F001", "업로드 된 파일이 없습니다."),
  FILE_NOT_FOUND(404, "F002", "해당 ID의 파일을 찾을 수 없습니다."),
  FILE_STORAGE_ERROR(500, "F003", "파일 저장 중 오류가 발생했습니다."),

  // backup
  BACKUP_NOT_FOUND(404, "B001", "백업을 찾을 수 없습니다."),
  BACKUP_NOT_IN_PROGRESS(409, "B002", "백업이 진행 중이어야만 처리할 수 있습니다."),
  BACKUP_REQUIRED_FILE(500, "B003", "백업 파일이 생성되지 않았습니다."),
  BACKUP_INVALID_STATUS(400, "B004", "유효하지 않은 백업 상태입니다."),
  BACKUP_CURSOR_ENCODE_FAILED(500, "B005", "커서 인코딩에 실패했습니다."),
  BACKUP_CURSOR_DECODE_FAILED(400, "B006", "커서 디코딩에 실패했습니다."),
  BACKUP_INVALID_SORT_FIELD(400, "B007", "유효하지 않은 정렬 기준입니다."),

  DUMMY_ERROR(500, "G002", "DUMMY");

  //


  private final int status;
  private final String code;
  private final String detail;


}
