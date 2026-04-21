package com.sb11.hr_bank.domain.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Schema(description = "부서 목록 조회 및 검색 요청")
public record DepartmentPageRequest(
    @Schema(description = "페이지 번호 (1부터 시작)", defaultValue = "1")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    Integer page,

    @Schema(description = "페이지 크기", defaultValue = "10")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    Integer size,

    @Schema(description = "정렬 기준 필드", example = "id")
    String sortBy,

    @Schema(description = "정렬 방향 (ASC/DESC)", example = "DESC")
    String direction,

    @Schema(description = "검색어 (부서명)", example = "개발")
    String searchName
) {

  public Pageable toPageable() {
    int pageNumber = (page != null && page > 0) ? page - 1 : 0;
    int pageSize = (size != null && size > 0) ? size : 10;

    Sort sort = Sort.by(Sort.Direction.DESC, "id");

    if (sortBy != null && !sortBy.isBlank()) {
      Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
      sort = Sort.by(sortDirection, sortBy);
    }

    return PageRequest.of(pageNumber, pageSize, sort);
  }
}