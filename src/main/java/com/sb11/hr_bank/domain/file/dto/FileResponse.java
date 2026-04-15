package com.sb11.hr_bank.domain.file.dto;

public record FileResponse(
    Long id,
    String name,
    String contentType,
    Long Size
) {
}
