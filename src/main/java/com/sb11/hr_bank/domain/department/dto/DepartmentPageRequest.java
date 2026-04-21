package com.sb11.hr_bank.domain.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Schema(description = "부서 목록 커서 기반 페이징 및 검색 요청")
public record DepartmentPageRequest(

    @Schema(description = "부서 이름 또는 설명", type = "string")
    String nameOrDescription,

    @Schema(description = "이전 페이지 마지막 요소 ID", type = "integer", format = "int64")
    Long idAfter,

    @Schema(description = "커서 (다음 페이지 시작점)", type = "string")
    String cursor,

    @Schema(description = "페이지 크기 (기본값: 10)", type = "integer", format = "int32", defaultValue = "10")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    Integer size,

    @Schema(description = "정렬 필드 (명세 호환용, 실제론 id 고정)", type = "string", defaultValue = "id")
    String sortField,

    @Schema(description = "정렬 방향 (명세 호환용, 실제론 desc 고정)", type = "string", defaultValue = "desc")
    String sortDirection
) {
  public Pageable toPageable() {
    int limitSize = (size != null && size > 0) ? size : 10;

    Sort sort = Sort.by(Sort.Direction.DESC, "id");

    return PageRequest.of(0, limitSize, sort);
  }
}