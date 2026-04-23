package com.sb11.hr_bank.domain.changelogs.service;

import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.dto.response.ChangeLogResponseDto;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeDetailLog;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLog;
import com.sb11.hr_bank.domain.changelogs.repository.ChangeLogRepository;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
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
  private final EmployeeRepository employeeRepository;

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

    // NPE 방어
    Slice<ChangeLogResponseDto.ListInfo> dtoSlice = logSlice.map(log -> {
      Employee employee = employeeRepository.findById(log.getEmployeeId())
              .orElse(null);
      String empNum = (employee != null)
          ? employee.getEmployeeNumber()
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
    Employee employee = employeeRepository.findById(log.getEmployeeId())
            .orElse(null);
    String empNum = (employee != null) ? employee.getEmployeeNumber() : "Unknown";
    String empName = (employee != null) ? employee.getName() : "탈퇴한 사용자";

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

  /**
   * 4. 변경 이력 생성 (다른 서비스에서 호출용)
   */
  @Transactional
  public void createLog(ChangeLogRequestDto.Create createDto, String ipAddress) {
    // 1. 로그 메인 엔티티 생성
    ChangeLog changeLog = ChangeLog.builder()
        .employeeId(createDto.getEmployeeId())
        .type(createDto.getType())
        .memo(createDto.getMemo())
        .ipAddress(ipAddress)
        .build();

    // 3. 상세 내역(Details) 추가
    if (createDto.getDetails() != null) {
      for (ChangeLogRequestDto.Create.Detail detailDto : createDto.getDetails()) {
        try {
          // String을 Enum(ChangeProperty)으로 변환하여 저장
          String propertyStr = detailDto.getPropertyName().toUpperCase();
          com.sb11.hr_bank.domain.changelogs.entity.ChangeProperty property =
              com.sb11.hr_bank.domain.changelogs.entity.ChangeProperty.valueOf(propertyStr);

          ChangeDetailLog detail = ChangeDetailLog.builder()
              .propertyName(property)
              .before(detailDto.getBefore())
              .after(detailDto.getAfter())
              .build();

          changeLog.addDetail(detail);
        } catch (IllegalArgumentException e) {
          // 잘못된 이름 들어와도 로그 생성 전체가 실패하지 않도록 방어
          System.out.println("알 수 없는 변경 항목명 무시됨: " + detailDto.getPropertyName());
        }

      }
    }

    // 4. 저장
    changeLogRepository.save(changeLog);
  }
}