package com.sb11.hr_bank.domain.changelogs.dto.request;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "변경 이력 조회 요청 조건")
  public static class Search {
    @Schema(description = "검색할 사번", example = "EMP20260423")
    private String employeeNumber;
    @Schema(description = "변경 작업 유형", example = "UPDATED")
    private ChangeLogType type;
    @Schema(description = "검색할 메모 내용(부분 검색)", example = "권한 수정")
    private String memo;
    @Schema(description = "특정 IP에서 작업한 내역 검색", example = "192.168.0.1")
    private String ipAddress;
    @Schema(description = "검색 시작 일시", type = "string", format = "date-time")
    private Instant atFrom;
    @Schema(description = "검색 종료 일시", type = "string", format = "date-time")
    private Instant atTo;

    // 이전 페이지 마지막 요소 ID (커서)
    @Schema(description = "다음 페이지 조회를 위한 커서 기준 ID. (첫 조회 시 비워둠)", example = "50")
    private Long idAfter;
    private String cursor;    // cursor or idAfter 둘 다받아서 서비스에서 처리

    private Integer size = 10;
    @Schema(description = "정렬 기준 필드 (at: 시간순, ipAddress: IP순)", example = "at", defaultValue = "at")
    private String sortField = "at";
    @Schema(description = "정렬 방향 (DESC: 최신순, ASC: 과거순", example = "DESC", defaultValue = "DESC")
    private String sortDirection = "DESC";

  }

}
