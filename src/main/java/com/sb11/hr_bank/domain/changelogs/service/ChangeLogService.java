package com.sb11.hr_bank.domain.changelogs.service;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeDetailLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeDetailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeDetailLogRepository changeDetailLogRepository;
  private final EmployeeRepository employeeRepository;

  // 이력 등록
  @Transactional
  public void createLog(ChangeLogRequestDto.Create request, String ipAddress) {
    // 프록시 객체만 가져와서 매핑
    Employee employee = employeeRepository.getReferenceById(request.getEmployeeId());

    ChangeLog changeLog = ChangeLog.builder()
        .employee(employee)
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
    Long cursorId = searchRequest.getLastId() != null ? searchRequest.getLastId() : Long.MAX_VALUE;

    // 페이지 크기 검증 로직
    Integer requestedSize = searchRequest.getSize();
    int size = (requestedSize == null || requestedSize < 1) ? 10 : Math.min(requestedSize, 100);


    // 정렬 로직
    // sortBy 필드를 Pageable Sort로 반영 "id"로 줘서 동시간대 여러개 들어와도 id순으로 정렬
    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    if (searchRequest.getSortBy() != null) {
      // sortDirection 안들어오면 기본값으로 DESC 사용
      String directionStr = searchRequest.getSortDirection() != null ? searchRequest.getSortDirection() : "DESC";
      sort = Sort.by(Sort.Direction.fromString(directionStr), searchRequest.getSortBy());
    }

    PageRequest pageRequest = PageRequest.of(0, size, sort);

    Slice<ChangeLog> logSlice = changeLogRepository.findByCursorPaging(
        cursorId,
        searchRequest.getEmployeeId(),
        searchRequest.getMemo(),
        searchRequest.getIpAddress(),
        searchRequest.getType(),
        searchRequest.getStartDate(),
        searchRequest.getEndDate(),
        pageRequest
    );

    return logSlice.getContent().stream()
        .map(log -> ChangeLogResponseDto.ListInfo.builder()
            .id(log.getId())
            .employeeId(log.getEmployee().getId())
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
