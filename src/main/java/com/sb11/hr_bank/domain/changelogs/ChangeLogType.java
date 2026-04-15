package com.sb11.hr_bank.domain.changelogs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum ChangeLogType {
  ADD("직원 추가"),
  UPDATE("정보 수정"),
  DELETE("직원 삭제");

  private final String description;
}
