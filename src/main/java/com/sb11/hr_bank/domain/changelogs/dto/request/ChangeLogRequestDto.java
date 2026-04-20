package com.sb11.hr_bank.domain.changelogs.dto.request;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import lombok.*;

import java.time.Instant;
import java.util.List;

public class ChangeLogRequestDto {
  // 생성 요청 DTO
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Create {
    private Long employeeId;
    private ChangeLogType type;
    private String memo;
    private List<Detail> details;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
      private String propertyName;
      private String before;
      private String after;
    }
  }

  // 검색 요청 DTO + 커서 페이징
  @Getter @Setter
  public static class Search {
    private Long employeeId;
    private String memo;
    private String ipAddress;
    private ChangeLogType type;
    private Instant startDate;
    private Instant endDate;

    // 이전 페이지 마지막 요소 ID (커서)
    private Long lastId;
    // ipAddress or createdAt (기본값)
    private String sortBy;
    private Integer size = 10;
  }


}
