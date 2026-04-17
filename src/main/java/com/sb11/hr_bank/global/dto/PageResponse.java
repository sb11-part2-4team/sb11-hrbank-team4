package com.sb11.hr_bank.global.dto;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public record PageResponse<T>(

    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

  public static <T> PageResponse<T> fromPage(Page<T> page){

    return new PageResponse<>(

        page.getContent(),
        null,
        null,
        page.getSize(),
        page.getTotalElements(),
        page.hasNext()

    );


  }
  public static <T> PageResponse<T> fromSlice(Slice<T> slice, String nextCursor, Long nextIdAfter){

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
