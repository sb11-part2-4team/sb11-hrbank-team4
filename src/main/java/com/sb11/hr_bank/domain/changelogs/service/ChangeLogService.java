package com.sb11.hr_bank.domain.changelogs.service;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.ErrorCode;
import com.sb11.hr_bank.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;

  /**
   * 1. 이력 목록 조회 (복합 커서 페이징 적용)
   */
  public PageResponse<ChangeLogResponseDto.ListInfo> getLogList(ChangeLogRequestDto.Search searchRequest) {

    // 페이지 사이즈 설정
    int size = (searchRequest.getSize() == null || searchRequest.getSize() < 1)
        ? 10 : Math.min(searchRequest.getSize(), 100);
    PageRequest pageRequest = PageRequest.of(0, size);

    // 커서 파라미터 초기화
    Instant atAfter = null;
    String ipAfter = null;
    Long idAfter = Long.MAX_VALUE;

    // 커서 데이터 조회 및 기준값 추출
    if (searchRequest.getIdAfter() != null) {
      ChangeLog cursorEntity = changeLogRepository.findById(searchRequest.getIdAfter())
          .orElseThrow(() -> new BusinessException(ErrorCode.CHANGELOG_INVALID_CURSOR));

      atAfter = cursorEntity.getCreatedAt();
      ipAfter = cursorEntity.getIpAddress();
      idAfter = cursorEntity.getId();
    }

    Slice<ChangeLog> logSlice;

    // 정렬 조건에 따른 메서드 분기
    if ("ipAddress".equals(searchRequest.getSortField())) {
      logSlice = changeLogRepository.findByCursorIpDesc(
          ipAfter, idAfter, searchRequest.getEmployeeNumber(), searchRequest.getMemo(),
          searchRequest.getIpAddress(), searchRequest.getType(),
          searchRequest.getAtFrom(), searchRequest.getAtTo(), pageRequest
      );
    } else {
      logSlice = changeLogRepository.findByCursorAtDesc(
          atAfter, idAfter, searchRequest.getEmployeeNumber(), searchRequest.getMemo(),
          searchRequest.getIpAddress(), searchRequest.getType(),
          searchRequest.getAtFrom(), searchRequest.getAtTo(), pageRequest
      );
    }

    // 엔티티 -> DTO 변환 (NPE 방어 로직 적용)
    Slice<ChangeLogResponseDto.ListInfo> dtoSlice = logSlice.map(log -> {
      String empNum = (log.getEmployee() != null)
          ? log.getEmployee().getEmployeeNumber()
          : "Unknown (Deleted)";

      return ChangeLogResponseDto.ListInfo.builder()
          .id(log.getId())
          .type(log.getType())
          .employeeNumber(empNum)
          .memo(log.getMemo())
          .ipAddress(log.getIpAddress())
          .at(log.getCreatedAt())
          .build();
    });

    Long nextIdAfter = (dtoSlice.hasNext() && !dtoSlice.isEmpty())
        ? dtoSlice.getContent().get(dtoSlice.getContent().size() - 1).getId()
        : null;

    return PageResponse.fromSlice(dtoSlice, null, nextIdAfter);
  }

  /**
   * 2. 이력 상세 조회
   */
  public ChangeLogResponseDto.DetailInfo getLogDetail(Long id) {
    ChangeLog log = changeLogRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHANGELOG_NOT_FOUND));

    // 직원 정보 NPE 방어
    String empNum = (log.getEmployee() != null) ? log.getEmployee().getEmployeeNumber() : "Unknown";
    String empName = (log.getEmployee() != null) ? log.getEmployee().getName() : "탈퇴한 사용자";

    return ChangeLogResponseDto.DetailInfo.builder()
        .id(log.getId())
        .type(log.getType())
        .employeeNumber(empNum)
        .employeeName(empName)
        .memo(log.getMemo())
        .ipAddress(log.getIpAddress())
        .at(log.getCreatedAt())
        // 상세 변경 내역 변환
        .diffs(log.getDetails().stream()
            .map(detail -> ChangeLogResponseDto.DetailInfo.DiffItem.builder()
                .propertyName(detail.getPropertyName().getDescription())
                .before(detail.getBefore())
                .after(detail.getAfter())
                .build())
            .collect(Collectors.toList()))
        .build();
  }

  /**
   * 3. 수정 이력 건수 조회
   */
  public long getLogCount(Instant fromDate, Instant toDate) {
    // null 체크 후 범위 설정
    Instant start = (fromDate != null) ? fromDate : Instant.parse("1970-01-01T00:00:00Z");
    Instant end = (toDate != null) ? toDate : Instant.now();

    return changeLogRepository.countByCreatedAtBetween(start, end);
  }
}