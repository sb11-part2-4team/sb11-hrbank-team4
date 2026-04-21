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
    private String employeeNumber;
    private ChangeLogType type;
    private String memo;
    private String ipAddress;
    private Instant atForm;
    private Instant atTo;

    // 이전 페이지 마지막 요소 ID (커서)
    private Long idAfter;
    private String cursor;    // cursor or idAfter 둘 다받아서 서비스에서 처리

    private Integer size = 10;
    private String sortField = "at";
    private String sortDirection = "desc";

  }

}
