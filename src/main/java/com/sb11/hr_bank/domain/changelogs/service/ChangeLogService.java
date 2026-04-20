package com.sb11.hr_bank.domain.changelogs.service;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeDetailLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeDetailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeDetailLogRepository changeDetailLogRepository;

  // 이력 등록
  @Transactional
  public void createLog(ChangeLogRequestDto.Create request, String ipAddress) {

    ChangeLog changeLog = ChangeLog.builder()
        .employeeId(request.getEmployeeId())
        .type(request.getType())
        .memo(request.getMemo())
        .ipAddress(ipAddress)
        .build();

    if (request.getDetails() != null) {
      for (ChangeLogRequestDto.Create.Detail detailDto : request.getDetails()) {
        ChangeDetailLog detailLog = ChangeDetailLog.builder()
            .propertyName(detailDto.getPropertyName())
            .before(detailDto.getBefore())
            .after(detailDto.getAfter())
            .build();
        changeLog.addDetail(detailLog);
      }
    }

    changeLogRepository.save(changeLog);

  }

  // 이력 목록 조회 (커서 페이징 처리)
  public List<ChangeLogResponseDto.ListInfo> getLogList(ChangeLogRequestDto.Search searchRequest) {
    // 첫 페이지일 경우 가장 큰 Long값 사용
    Long cursorId = searchRequest.getLastId() != null ? searchRequest.getLastId() : Long.MAX_VALUE;
    PageRequest pageRequest = PageRequest.of(0, searchRequest.getSize());
    Slice<ChangeLog> logSlice = changeLogRepository.findByCursorPaging(cursorId, pageRequest);

    return logSlice.getContent().stream()
        .map(log -> ChangeLogResponseDto.ListInfo.builder()
            .id(log.getId())
            .employeeId(log.getEmployeeId())
            .type(log.getType())
            .memo(log.getMemo())
            .ipAddress(log.getIpAddress())
            .createdAt(log.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }

    // 이력 상세 내용 조회
    public ChangeLogResponseDto.DetailInfo getLogDetail(Long changeLogId) {
      List<ChangeDetailLog> details = changeDetailLogRepository.findByChangeLogId(changeLogId);

      List<ChangeLogResponseDto.DetailInfo.DetailItem> detailItems = details.stream()
          .map(detail -> ChangeLogResponseDto.DetailInfo.DetailItem.builder()
              .propertyName(detail.getPropertyName())
              .before(detail.getBefore())
              .after(detail.getAfter())
              .build())
          .collect(Collectors.toList());

      return ChangeLogResponseDto.DetailInfo.builder()
          .id(changeLogId)
          .details(detailItems)
          .build();
    }




  }


}
