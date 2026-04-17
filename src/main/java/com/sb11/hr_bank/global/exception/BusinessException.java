package com.sb11.hr_bank.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode code;

  public BusinessException(ErrorCode code) {
    this.code = code;
  }
}
