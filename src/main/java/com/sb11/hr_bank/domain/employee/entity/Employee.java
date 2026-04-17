package com.sb11.hr_bank.domain.employee.entity;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.file.entity.FileEntity;
import com.sb11.hr_bank.global.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "employees")
@NoArgsConstructor
public class Employee extends BaseUpdatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "employee_number", nullable = false, length = 100)
    private String employeeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EmployeeStatus employeeStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id")
    private FileEntity profileImage;

    public Employee(String name, String email, String employeeNumber, Department department,
                    String position, LocalDate hireDate, FileEntity profileImage) {
        this.name = name;
        this.email = email;
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.employeeStatus = EmployeeStatus.ACTIVE;
        this.profileImage = profileImage;
    }

    public void update(String name, String email, Department department,
                  String position, LocalDate hireDate,
                  EmployeeStatus employeeStatus, FileEntity profileImage) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.employeeStatus = employeeStatus;
        this.profileImage = profileImage;
    }
}