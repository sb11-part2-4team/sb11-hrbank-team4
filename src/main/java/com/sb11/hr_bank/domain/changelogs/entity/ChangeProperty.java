package com.sb11.hr_bank.domain.changelogs.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChangeProperty {
  NAME("이름"),
  EMAIL("이메일"),
  DEPARTMENT("부서"),
  POSITION("직함"),
  HIRE_DATE("고용일"),
  STATUS("상태"),
  PROFILE("프로필");

  private final String description;
}
