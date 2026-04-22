package com.sb11.hr_bank.domain.changelogs.service;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeDetailLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeProperty;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeDetailLogRepository;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeDetailLogRepository changeDetailLogRepository;
  private final EmployeeRepository employeeRepository;

  @Transactional
  public void createLog(ChangeLogRequestDto.Create request, String ipAddress) {
    // 1. 실제로 직원이 존재하는지 체크는 하되, 연관관계는 프록시로 세팅 (성능 최적화)
    if (!employeeRepository.existsById(request.getEmployeeId())) {
      throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }
    Employee employee = employeeRepository.getReferenceById(request.getEmployeeId());

    ChangeLog changeLog = ChangeLog.builder()
        .employee(employee)
        .type(request.getType())
        .memo(request.getMemo())
        .ipAddress(ipAddress)
        .build();

    if (request.getDetails() != null) {
      for (ChangeLogRequestDto.Create.Detail detailDto : request.getDetails()) {
        // 2. 한글명(description)으로 Enum을 찾아오는 안전한 방식 사용
        ChangeProperty property = Arrays.stream(ChangeProperty.values())
            .filter(p -> p.getDescription().equals(detailDto.getPropertyName()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.CHANGELOG_INVALID_PROPERTY_NAME));

        ChangeDetailLog detailLog = ChangeDetailLog.builder()
            .propertyName(property)
            .before(detailDto.getBefore())
            .after(detailDto.getAfter())
            .build();
        changeLog.addDetail(detailLog);
      }
    }
    changeLogRepository.save(changeLog);
  }

  public PageResponse<ChangeLogResponseDto.ListInfo> getLogList(ChangeLogRequestDto.Search searchRequest) {
    int size = (searchRequest.getSize() == null || searchRequest.getSize() < 1) ? 10 : Math.min(searchRequest.getSize(), 100);
    String directionStr = (searchRequest.getSortDirection() != null) ? searchRequest.getSortDirection().toUpperCase() : "DESC";
    boolean isAsc = "ASC".equals(directionStr);

    Long reqCursor = searchRequest.getIdAfter();
    if (reqCursor == null && searchRequest.getCursor() != null) {
      try {
        reqCursor = Long.parseLong(searchRequest.getCursor());
      } catch (NumberFormatException e) {
        throw new BusinessException(ErrorCode.CHANGELOG_INVALID_CURSOR);
      }
    }

    Long cursorId;
    PageRequest pageRequest;
    Slice<ChangeLog> logSlice;

    // 3. ASC/DESC에 따른 메서드 호출 오류 수정
    if (isAsc) {
      cursorId = reqCursor != null ? reqCursor : 0L;
      pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "id"));
      logSlice = changeLogRepository.findByCursorPagingAsc( // Asc 호출로 수정
          cursorId, searchRequest.getEmployeeNumber(), searchRequest.getMemo(), searchRequest.getIpAddress(),
          searchRequest.getType(), searchRequest.getAtFrom(), searchRequest.getAtTo(), pageRequest
      );
    } else {
      cursorId = reqCursor != null ? reqCursor : Long.MAX_VALUE;
      pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
      logSlice = changeLogRepository.findByCursorPagingDesc( // Desc 호출로 수정
          cursorId, searchRequest.getEmployeeNumber(), searchRequest.getMemo(), searchRequest.getIpAddress(),
          searchRequest.getType(), searchRequest.getAtFrom(), searchRequest.getAtTo(), pageRequest
      );
    }

    Slice<ChangeLogResponseDto.ListInfo> dtoSlice = logSlice.map(log -> ChangeLogResponseDto.ListInfo.builder()
        .id(log.getId())
        .type(log.getType())
        .employeeNumber(log.getEmployee().getEmployeeNumber())
        .memo(log.getMemo())
        .ipAddress(log.getIpAddress())
        .at(log.getCreatedAt())
        .build()
    );

    Long nextIdAfter = null;
    if (dtoSlice.hasNext() && !dtoSlice.isEmpty()) {
      List<ChangeLogResponseDto.ListInfo> content = dtoSlice.getContent();
      nextIdAfter = content.get(content.size() - 1).getId();
    }

    return PageResponse.fromSlice(dtoSlice, null, nextIdAfter);
  }

  public ChangeLogResponseDto.DetailInfo getLogDetail(Long changeLogId) {
    ChangeLog changeLog = changeLogRepository.findById(changeLogId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHANGELOG_NOT_FOUND));

    List<ChangeDetailLog> details = changeDetailLogRepository.findByChangeLogId(changeLogId);

    List<ChangeLogResponseDto.DetailInfo.DiffItem> diffItems = details.stream()
        .map(detail -> ChangeLogResponseDto.DetailInfo.DiffItem.builder()
            .propertyName(detail.getPropertyName().getDescription()) // 4. Enum -> 한글명 변환 추가
            .before(detail.getBefore())
            .after(detail.getAfter())
            .build())
        .collect(Collectors.toList());

    return ChangeLogResponseDto.DetailInfo.builder()
        .id(changeLog.getId())
        .type(changeLog.getType())
        .employeeNumber(changeLog.getEmployee().getEmployeeNumber())
        .memo(changeLog.getMemo())
        .ipAddress(changeLog.getIpAddress())
        .at(changeLog.getCreatedAt())
        .employeeName(changeLog.getEmployee().getName())
        .profileImageId(changeLog.getEmployee().getProfileImage() != null ? changeLog.getEmployee().getProfileImage().getId() : null)
        .diffs(diffItems)
        .build();
  }

  public long getLogCount(Instant fromDate, Instant toDate) {
    Instant end = toDate != null ? toDate : Instant.now();
    Instant start = fromDate != null ? fromDate : end.minus(7, ChronoUnit.DAYS);
    return changeLogRepository.countByCreatedAtBetween(start, end);
  }
}