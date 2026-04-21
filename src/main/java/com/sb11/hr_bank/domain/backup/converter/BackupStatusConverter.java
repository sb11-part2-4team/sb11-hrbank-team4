package com.sb11.hr_bank.domain.backup.converter;

import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BackupStatusConverter implements Converter<String, BackupStatus> {

  public BackupStatus convert(String source) {
    return BackupStatus.fromDescription(source);
  }

}
