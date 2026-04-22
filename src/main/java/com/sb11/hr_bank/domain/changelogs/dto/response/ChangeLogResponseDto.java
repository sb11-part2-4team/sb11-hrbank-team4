package com.sb11.hr_bank.domain.changelogs.dto.response;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

public class ChangeLogResponseDto {

  // 목록 조회용 DTO
  @Getter
  @Builder
  public static class ListInfo {
    private Long id;
    private ChangeLogType type;
    private String employeeNumber;
    private String memo;
    private String ipAddress;
    private Instant at;
  }

  // 상세 조회용 DTO
  @Getter
  @Builder
  public static class DetailInfo {

    private Long id;
    private ChangeLogType type;
    private String employeeNumber;
    private String memo;
    private String ipAddress;
    private Instant at;
    private String employeeName;
    private Long profileImageId;
    private List<DiffItem> diffs;

    @Getter
    @Builder
    public static class DiffItem{
      private String propertyName;
      private String before;
      private String after;
    }
  }

}
