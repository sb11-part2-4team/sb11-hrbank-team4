package com.sb11.hr_bank.domain.changelogs.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class ChangePropertyConverter implements AttributeConverter<ChangeProperty, String> {

  @Override
  public String convertToDatabaseColumn(ChangeProperty attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getDescription();
  }

  @Override
  public ChangeProperty convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Arrays.stream(ChangeProperty.values())
        .filter(property -> property.getDescription().equals(dbData))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 수정 항목입니다: " + dbData));
  }
}