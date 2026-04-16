package com.sb11.hr_bank.domain.employee.controller;

import com.sb11.hr_bank.domain.employee.dto.EmployeeCountCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCreateRequest;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDto;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeUpdateRequest;
import com.sb11.hr_bank.domain.employee.service.EmployeeService;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/employees")
@RequiredArgsConstructor
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> create (
            @RequestPart("employeeCreateRequest") EmployeeCreateRequest dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException{
        FileEntity fileEntity = null;
        if(profile != null && !profile.isEmpty()) {
            fileEntity = fileService.uploadFile(profile);
        }
        EmployeeDto result = employeeService.create(dto, fileEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping(value = "/{employeeId}")
    public ResponseEntity<EmployeeDto> findById(
            @PathVariable Long employeeId
    ) {
        EmployeeDto result = employeeService.findById(employeeId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> findAll(
            @ModelAttribute EmployeeSearchCondition condition
    ) {
        List<EmployeeDto> result = employeeService.findAllByCondition(condition);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @ModelAttribute EmployeeCountCondition condition
    ) {
        Long result = employeeService.countByCondition(condition);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(
            @PathVariable Long employeeId,
            @RequestPart("employee") EmployeeUpdateRequest dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        FileEntity fileEntity = null;
        if(profile != null && !profile.isEmpty()) {
            fileEntity = fileService.uploadFile(profile);
        }
        employeeService.update(employeeId, dto, fileEntity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{employeeId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long employeeId
    ) {
        employeeService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
