package com.sb11.hr_bank.domain.employee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb11.hr_bank.domain.changelogs.dto.request.ChangeLogRequestDto;
import com.sb11.hr_bank.domain.changelogs.entity.ChangeLogType;
import com.sb11.hr_bank.domain.changelogs.service.ChangeLogService;
import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.department.repository.DepartmentRepository;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCountCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCreateRequest;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCursor;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionRow;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeTrendCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeTrendDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import com.sb11.hr_bank.domain.employee.mapper.EmployeeMapper;
import com.sb11.hr_bank.domain.employee.mapper.EmployeePageResponseMapper;
import com.sb11.hr_bank.domain.employee.repository.EmployeeRepository;
import com.sb11.hr_bank.domain.employee.repository.EmployeeSpecifications;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.service.FileService;
import com.sb11.hr_bank.global.dto.PageResponse;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final FileService fileService;
    private final ChangeLogService changeLogService;
    private final EmployeeMapper employeeMapper;
    private final EmployeePageResponseMapper employeePageResponseMapper;
    private final ObjectMapper objectMapper;

    public EmployeeDto create(EmployeeCreateRequest dto, MultipartFile profile) {
        if(employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException(ErrorCode.EMPLOYEE_DUPLICATE_EMAIL);
        }

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_DEPARTMENT_NOT_FOUND));

        LocalDate hireDate = dto.hireDate();
        int year = hireDate.getYear();
        int nextSequence = employeeRepository.findMaxEmployeeNumberByYear(year)
                .map(employeeNumber -> Integer.parseInt(employeeNumber.substring(employeeNumber.lastIndexOf("-") + 1)) + 1)
                .orElse(1);
        String employeeNumber = String.format("EMP-%d-%03d", hireDate.getYear(), nextSequence);

        FileEntity file = uploadProfileIfPresent(profile);
        try {
            Employee employee = new Employee(
                    dto.name(),
                    dto.email(),
                    employeeNumber,
                    department,
                    dto.position(),
                    dto.hireDate(),
                    file
            );
            employeeRepository.save(employee);

            List<ChangeLogRequestDto.Create.Detail> details = new ArrayList<>();
            details.add(detail("이름", null, employee.getName()));
            details.add(detail("이메일", null, employee.getEmail()));
            details.add(detail("부서", null, employee.getDepartment().getName()));
            details.add(detail("직함", null, employee.getPosition()));
            details.add(detail("고용일", null, employee.getHireDate().toString()));
            details.add(detail("상태", null, employee.getEmployeeStatus().getLabel()));
            addDetailIfChanged(details, "프로필", null, profileIdText(file));
            createChangeLog(employee, ChangeLogType.ADD, dto.memo(), details);

            return employeeMapper.toDto(employee);
        } catch (RuntimeException e) {
            cleanupUploadedFile(file);
            throw e;
        }

    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        return employeeMapper.toDto(employee);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeDto> findAllByCondition(EmployeeSearchCondition condition) {
        int size = condition == null || condition.size() == null || condition.size() <= 0
                ? 10
                : condition.size();
        EmployeeCursor cursor = decodeCursor(condition);
        Sort.Direction direction = sortDirection(condition);
        String sortField = sortField(condition);

        Page<Employee> page = employeeRepository.findAll(
                EmployeeSpecifications.searchCondition(condition)
                        .and(EmployeeSpecifications.cursorCondition(cursor)),
                PageRequest.of(
                        0,
                        size + 1,
                        Sort.by(direction, sortField).and(Sort.by(direction, "id"))
                )
        );

        List<Employee> pageContent = page.getContent();
        Long totalElements = employeeRepository.count(EmployeeSpecifications.searchCondition(condition));

        if(pageContent.isEmpty()) {
            return employeePageResponseMapper.toPageResponse(
                    pageContent,
                    null,
                    null,
                    size,
                    totalElements,
                    false
            );
        }

        boolean hasNext = pageContent.size() > size;
        Employee last = hasNext ? pageContent.get(size - 1) : null;

        return employeePageResponseMapper.toPageResponse(
                hasNext ? pageContent.subList(0, size) : pageContent,
                last != null ? encodeCursor(last, condition) : null,
                last != null ? last.getId() : null,
                size,
                totalElements,
                hasNext
        );
    }

    @Transactional(readOnly = true)
    public Long countByCondition(EmployeeCountCondition condition) {
        if(condition == null) {
            return employeeRepository.count(EmployeeSpecifications.countCondition(null));
        }

        LocalDate fromDate = condition.fromDate();
        LocalDate toDate = condition.toDate();

        if(fromDate != null && toDate == null) {
            toDate = LocalDate.now();
        }

        if(fromDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_DATE_RANGE);
        }

        EmployeeCountCondition normalizedCondition = new EmployeeCountCondition(
                condition.status(),
                fromDate,
                toDate
        );

        return employeeRepository.count(EmployeeSpecifications.countCondition(normalizedCondition));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDistributionDto> getDistribution(EmployeeDistributionCondition condition) {
        String groupBy = condition != null && condition.groupBy() != null && !condition.groupBy().isBlank()
                ? condition.groupBy()
                : "department";

        EmployeeStatus status = condition != null && condition.status() != null
                ? condition.status()
                : EmployeeStatus.ACTIVE;

        List<EmployeeDistributionRow> rows = employeeRepository.findDistribution(groupBy, status);

        long total = rows.stream()
                .mapToLong(EmployeeDistributionRow::count)
                .sum();

        return rows.stream()
                .map(row -> new EmployeeDistributionDto(
                        row.groupKey(),
                        row.count(),
                        calculateRate(row.count(), total)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition) {
        String unit = condition != null && condition.unit() != null && !condition.unit().isBlank()
                ? condition.unit()
                : "month";

        if(!List.of("day", "week", "month", "quarter", "year").contains(unit)) {
            throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
        }

        LocalDate to = condition != null && condition.to() != null
                ? condition.to()
                : LocalDate.now();

        LocalDate from = condition != null && condition.from() != null
                ? condition.from()
                : switch (unit) {
                    case "day" -> to.minusDays(11);
                    case "week" -> to.minusWeeks(11);
                    case "month" -> to.minusMonths(11);
                    case "quarter" -> to.minusMonths(33);
                    case "year" -> to.minusYears(11);
                    default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
                };

        if(from.isAfter(to)) {
            throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_DATE_RANGE);
        }

        LocalDate bucketFrom = startOfBucket(from, unit);
        LocalDate bucketTo = startOfBucket(to, unit);

        List<LocalDate> buckets = new ArrayList<>();
        LocalDate current = bucketFrom;

        while(!current.isAfter(bucketTo)) {
            buckets.add(current);
            current = nextDate(current, unit);
        }

        List<EmployeeTrendDto> result = new ArrayList<>();
        long previousCount = employeeRepository.countByHireDateLessThan(from);
        long count = previousCount;

        for(LocalDate bucket : buckets) {
            LocalDate bucketStart = bucket.isBefore(from) ? from : bucket;
            LocalDate bucketEnd = nextDate(bucket, unit).minusDays(1);
            bucketEnd = bucketEnd.isAfter(to) ? to : bucketEnd;

            long change = employeeRepository.countByHireDateBetween(bucketStart, bucketEnd);
            count += change;

            result.add(new EmployeeTrendDto(
                    bucket.toString(),
                    count,
                    change,
                    calculateRate(change, previousCount)
            ));

            previousCount = count;
        }

        return result;
    }

    public EmployeeDto update(Long id, EmployeeUpdateRequest dto, MultipartFile profile) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if(!employee.getEmail().equals(dto.email())
                && employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException(ErrorCode.EMPLOYEE_DUPLICATE_EMAIL);
        }

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_DEPARTMENT_NOT_FOUND));

        String beforeName = employee.getName();
        String beforeEmail = employee.getEmail();
        String beforeDepartmentName = employee.getDepartment().getName();
        String beforePosition = employee.getPosition();
        String beforeHireDate = employee.getHireDate().toString();
        String beforeStatus = employee.getEmployeeStatus().getLabel();
        String beforeProfileId = profileIdText(employee.getProfileImage());

        FileEntity oldProfile = employee.getProfileImage();
        FileEntity newProfile = uploadProfileIfPresent(profile);
        FileEntity fileEntity = newProfile != null ? newProfile : oldProfile;

        try {
            employee.update(
                    dto.name(),
                    dto.email(),
                    department,
                    dto.position(),
                    dto.hireDate(),
                    dto.status(),
                    fileEntity
            );
            employeeRepository.flush();

            if(newProfile != null && oldProfile != null) {
                fileService.deleteFile(oldProfile.getId());
            }

            List<ChangeLogRequestDto.Create.Detail> details = new ArrayList<>();
            addDetailIfChanged(details, "이름", beforeName, employee.getName());
            addDetailIfChanged(details, "이메일", beforeEmail, employee.getEmail());
            addDetailIfChanged(details, "부서", beforeDepartmentName, employee.getDepartment().getName());
            addDetailIfChanged(details, "직함", beforePosition, employee.getPosition());
            addDetailIfChanged(details, "고용일", beforeHireDate, employee.getHireDate().toString());
            addDetailIfChanged(details, "상태", beforeStatus, employee.getEmployeeStatus().getLabel());
            addDetailIfChanged(details, "프로필", beforeProfileId, profileIdText(employee.getProfileImage()));
            createChangeLog(employee, ChangeLogType.UPDATE, dto.memo(), details);

            return employeeMapper.toDto(employee);
        } catch (RuntimeException e) {
            cleanupUploadedFile(newProfile);
            throw e;
        }

    }

    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        FileEntity profile = employee.getProfileImage();

        List<ChangeLogRequestDto.Create.Detail> details = new ArrayList<>();
        details.add(detail("이름", employee.getName(), null));
        details.add(detail("이메일", employee.getEmail(), null));
        details.add(detail("부서", employee.getDepartment().getName(), null));
        details.add(detail("직함", employee.getPosition(), null));
        details.add(detail("고용일", employee.getHireDate().toString(), null));
        details.add(detail("상태", employee.getEmployeeStatus().getLabel(), null));
        addDetailIfChanged(details, "프로필", profileIdText(profile), null);
        createChangeLog(employee, ChangeLogType.DELETE, null, details);

        employeeRepository.delete(employee);
        employeeRepository.flush();

        if(profile != null) {
            fileService.deleteFile(profile.getId());
        }
    }

    private double calculateRate(long value, long total) {
        if(total == 0) {
            return 0.0;
        }

        return Math.round(value * 1000.0 / total) / 10.0;
    }

    private LocalDate startOfBucket(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date;
            case "week" -> date.minusDays(date.getDayOfWeek().getValue() - 1);
            case "month" -> date.withDayOfMonth(1);
            case "quarter" -> date.withMonth(((date.getMonthValue() - 1) / 3) * 3 + 1).withDayOfMonth(1);
            case "year" -> date.withDayOfYear(1);
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
        };
    }

    private LocalDate nextDate(LocalDate date, String unit) {
        return switch (unit) {
            case "day" -> date.plusDays(1);
            case "week" -> date.plusWeeks(1);
            case "month" -> date.plusMonths(1);
            case "quarter" -> date.plusMonths(3);
            case "year" -> date.plusYears(1);
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_TREND_UNIT);
        };
    }

    private Sort.Direction sortDirection(EmployeeSearchCondition condition) {
        if(condition == null || condition.sortDirection() == null || condition.sortDirection().isBlank()) {
            return Sort.Direction.ASC;
        }

        return switch (condition.sortDirection().toLowerCase()) {
            case "asc" -> Sort.Direction.ASC;
            case "desc" -> Sort.Direction.DESC;
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_SORT_DIRECTION);
        };
    }

    private String sortField(EmployeeSearchCondition condition) {
        if(condition == null || condition.sortField() == null || condition.sortField().isBlank()) {
            return "name";
        }

        return switch (condition.sortField()) {
            case "name", "employeeNumber", "hireDate" -> condition.sortField();
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_SORT_FIELD);
        };
    }

    private String encodeCursor(Employee employee, EmployeeSearchCondition condition) {
        String sortField = sortField(condition);

        Map<String, Object> cursor = new LinkedHashMap<>();
        cursor.put(sortField, switch (sortField) {
            case "name" -> employee.getName();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "hireDate" -> employee.getHireDate().toString();
            default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_SORT_FIELD);
        });

        try {
            byte[] json = objectMapper.writeValueAsBytes(cursor);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.EMPLOYEE_CURSOR_ENCODING_FAILED);
        }
    }

    private EmployeeCursor decodeCursor(EmployeeSearchCondition condition) {
        if(condition == null || condition.cursor() == null || condition.cursor().isBlank()
                || condition.idAfter() == null) {
            return null;
        }

        String sortField = sortField(condition);

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(condition.cursor());
            JsonNode json = objectMapper.readTree(decoded);

            if(!json.hasNonNull(sortField)) {
                throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_CURSOR);
            }

            return new EmployeeCursor(
                    sortField,
                    json.get(sortField).asText(),
                    condition.idAfter(),
                    sortDirection(condition).name()
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_CURSOR);
        }
    }

    private FileEntity uploadProfileIfPresent(MultipartFile profile) {
        if(profile == null || profile.isEmpty()) {
            return null;
        }

        return fileService.uploadFile(profile);
    }

    private void cleanupUploadedFile(FileEntity file) {
        if(file == null) {
            return;
        }

        try {
            fileService.cleanupDummyFile(file.getId());
        } catch (Exception e) {
            log.warn("업로드 파일 정리 실패. fileId={}", file.getId(), e);
        }
    }

    private ChangeLogRequestDto.Create.Detail detail(String propertyName, String before, String after) {
        return ChangeLogRequestDto.Create.Detail.builder()
                .propertyName(propertyName)
                .before(before)
                .after(after)
                .build();
    }

    private void addDetailIfChanged(
            List<ChangeLogRequestDto.Create.Detail> details,
            String propertyName,
            String before,
            String after
    ) {
        if(!Objects.equals(before, after)) {
            details.add(detail(propertyName, before, after));
        }
    }

    private String profileIdText(FileEntity file) {
        return file != null && file.getId() != null ? String.valueOf(file.getId()) : null;
    }

    private void createChangeLog(
            Employee employee,
            ChangeLogType type,
            String memo,
            List<ChangeLogRequestDto.Create.Detail> details
    ) {
        changeLogService.createLog(
                ChangeLogRequestDto.Create.builder()
                        .employeeId(employee.getId())
                        .type(type)
                        .memo(memo)
                        .details(details)
                        .build(),
                currentIpAddress()
        );
    }

    private String currentIpAddress() {
        if(!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return "127.0.0.1";
        }

        String ipAddress = attributes.getRequest().getRemoteAddr();
        return "0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)
                ? "127.0.0.1"
                : ipAddress;
    }
}

