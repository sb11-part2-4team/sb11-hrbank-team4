package com.sb11.hr_bank.domain.changelogs.dto;

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
    private Long employeeId;
    private ChangeLogType type;
    private String memo;
    private String ipAddress;
    private Instant createdAt;

  }

  // 상세 조회용 DTO
  @Getter
  @Builder
  public static class DetailItem {

    private Long id;
    private List<DetailItem> details;

    @Getter
    @Builder
    public static class DetailItem{
      private String propertyName;
      private String before;
      private String after;
    }
  }

}
