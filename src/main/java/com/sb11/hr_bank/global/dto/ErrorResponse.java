package com.sb11.hr_bank.global.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorResponse {

  private final Instant timestamp;
  private final int status;
  private final String message;
  @Setter
  private String details;


  public ErrorResponse(int status, String details) {
    this.timestamp = Instant.now();
    this.status = status;
    this.message = resolveMessage(status);
    this.details = details;
  }

  //상태 코드에 따라 메시지는 고정. 세부 메시지는 ErrorCode에서.
  private String resolveMessage(int status){

    return switch (status) {
      case (400) -> "잘못된 요청입니다.";
      case (404) -> "요청하신 자원이 존재하지 않습니다.";
      case (409) -> "현재 해당 요청을 처리할 수 없습니다.";
      case (500) -> "요청 처리중 오류가 발생했습니다.";
      default -> "요청 처리중 오류가 발생했습니다. ";
    };




  }


}
