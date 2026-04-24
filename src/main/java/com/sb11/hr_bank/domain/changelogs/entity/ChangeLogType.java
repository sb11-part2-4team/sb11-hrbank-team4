package com.sb11.hr_bank.domain.changelogs.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChangeLogType {
  CREATED("직원 추가"),
  UPDATED("정보 수정"),
  DELETED("직원 삭제");

  private final String description;
}
