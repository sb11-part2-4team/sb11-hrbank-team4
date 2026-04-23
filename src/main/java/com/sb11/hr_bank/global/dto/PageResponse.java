package com.sb11.hr_bank.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public record PageResponse<T>(

    List<T> content,

    @Schema(example = "eyJpZCI6MjB9")
    String nextCursor,

    @Schema(example = "20")
    Long nextIdAfter,

    @Schema(example = "10")
    int size,

    @Schema(example = "100")
    Long totalElements,

    @Schema(example = "true")
    boolean hasNext
) {


  public static <T> PageResponse<T> fromPage(Page<T> page) {

    return new PageResponse<>(

        page.getContent(),
        null,
        null,
        page.getSize(),
        page.getTotalElements(),
        page.hasNext()

    );


  }

  public static <T> PageResponse<T> fromSlice(Slice<T> slice, String nextCursor, Long nextIdAfter) {

    return new PageResponse<>(

        slice.getContent(),
        nextCursor,
        nextIdAfter,
        slice.getSize(),
        null,
        slice.hasNext()
    );
  }


}
