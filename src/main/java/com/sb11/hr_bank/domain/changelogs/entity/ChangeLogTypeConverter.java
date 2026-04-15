package com.sb11.hr_bank.domain.changelogs.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

// JPA에게 이 클래스가 컨버터라는것 알림
// autoApply = true : 엔티티에 하나하나 컨버터 연결 안해도 Enum 만날때마다 Converter 참조
@Converter(autoApply = true)
// JPA가 AttributeConverter를 제공해서 ChangeLogType과 String 통역 가능
public class ChangeLogTypeConverter implements AttributeConverter<ChangeLogType, String> {

  // TODO : ADD, UPDATE, DELETE 를 직원 추가, 정보 수정, 직원 삭제 로 변환 코드 작성

  // Enum -> DB(String) 저장, DB에 INSERT/UPDATE 할 때 자동 실행
  @Override
  public String convertToDatabaseColumn(ChangeLogType attribute) {
    // NPE 방지, null 들어가도 Column(nullable = false) 해놔서 DB 제약조건 위반 에러 발생
    if (attribute == null) {
      return null;
    }
    return attribute.getDescription();
  }

  // DB(String) SELECT -> Enum 읽기
  @Override
  public ChangeLogType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    // 직원추가, 정보변경, 직원삭제 를 가져와서 ADD, UPDATE, DELETE와 비교 준비
    return Arrays.stream(ChangeLogType.values())
        .filter(type -> type.getDescription().equals(dbData)) // DB의 String과 Enum 비교
        .findFirst() // 비교해서 걸러낸것 중 첫번째 찾기
        .orElseThrow(() -> new IllegalArgumentException("Test: 알 수 없는 타입 " + dbData)); // DB에 오타 Type 들어있을 경우 에러 메시지
  }

}
