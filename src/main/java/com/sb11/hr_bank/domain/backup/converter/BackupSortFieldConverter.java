package com.sb11.hr_bank.domain.backup.converter;

import com.sb11.hr_bank.domain.backup.query.BackupSortField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BackupSortFieldConverter implements Converter<String, BackupSortField> {

  public BackupSortField convert(String source) {
    return BackupSortField.from(source);
  }

}
