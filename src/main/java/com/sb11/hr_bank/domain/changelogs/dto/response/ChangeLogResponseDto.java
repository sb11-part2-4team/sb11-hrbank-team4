package com.sb11.hr_bank.domain.changelogs.dto.response;

import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

public class ChangeLogResponseDto {

  // 목록 조회용 DTO
  @Getter
  @Builder
  @Schema(description = "변경 이력 목록 요약 정보")
  public static class ListInfo {
    @Schema(description = "로그 고유 ID", example = "102")
    private Long id;
    @Schema(description = "변경 작업 유형", example = "UPDATED")
    private ChangeLogType type;
    @Schema(description = "작업 대상 사원 번호", example = "EMP20260423")
    private String employeeNumber;
    @Schema(description = "작업 메모", example = "부서 이동 처리")
    private String memo;
    @Schema(description = "작업자 IP 주소", example = "192.168.0.1")
    private String ipAddress;
    @Schema(description = "로그 생성 일시")
    private Instant at;
  }

  // 상세 조회용 DTO
  @Getter
  @Builder
  public static class DetailInfo {

    @Schema(description = "로그 고유 ID", example = "102")
    private Long id;
    @Schema(description = "변경 작업 유형", example = "UPDATE")
    private ChangeLogType type;
    @Schema(description = "작업 대상 사원 번호", example = "EMP20260423")
    private String employeeNumber;
    @Schema(description = "작업 메모", example = "인사 정보 최신화")
    private String memo;
    @Schema(description = "작업자 IP 주소", example = "192.168.0.18")
    private String ipAddress;
    @Schema(description = "로그 생성 일시")
    private Instant at;
    @Schema(description = "작업 대상 사원 이름", example = "홍길동")
    private String employeeName;
    @Schema(description = "작업 대상 사원 프로필 이미지 ID", example = "505")
    private Long profileImageId;
    @Schema(description = "상세 변경 항목 리스트")
    private List<DiffItem> diffs;

    @Getter
    @Builder
    @Schema(description = "개별 항목 변경 전/후 비교 정보")
    public static class DiffItem{
      @Schema(description = "변경된 속성 명칭", example = "부서")
      private String propertyName;
      @Schema(description = "변경 전 값", example = "개발팀")
      private String before;
      @Schema(description = "변경 후 값", example = "인사팀")
      private String after;
    }
  }

}
