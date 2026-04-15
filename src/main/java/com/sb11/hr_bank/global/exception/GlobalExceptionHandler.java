package com.sb11.hr_bank.global.exception;


import com.sb11.hr_bank.global.dto.ErrorResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {


  //커스텀 예외 핸들러
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

    ErrorCode errorCode = e.getCode();
    ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus(), errorCode.getDetail());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }




  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    ErrorResponse errorResponse = new ErrorResponse(400,e.getMessage());
    return ResponseEntity.status(400).body(errorResponse);
  }


  @ExceptionHandler(IOException.class)
  public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
    ErrorResponse errorResponse = new ErrorResponse(500,e.getMessage());
    return ResponseEntity.status(500).body(errorResponse);
  }



  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {

    ErrorResponse errorResponse = new ErrorResponse(500,"서버 내부에서 예상치 못한 오류가 발생했습니다.");
    errorResponse.setDetails(e.getMessage());
    return ResponseEntity.status(500).body(errorResponse);



  }

}
