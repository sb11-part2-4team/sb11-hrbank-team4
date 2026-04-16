package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecifications {

    public static Specification<Employee> search(EmployeeSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(condition == null) {
                return cb.conjunction();
            }

            if(hasText(condition.nameOrEmail())) {
                String keyword = contains(condition.nameOrEmail());

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keyword),
                        cb.like(cb.lower(root.get("email")), keyword)
                ));
            }

            if(hasText(condition.employeeNumber())) {
                predicates.add(cb.like(
                        cb.lower(root.get("employeeNumber")),
                        contains(condition.employeeNumber())
                ));
            }

            if(hasText(condition.departmentName())) {
                Join<Employee, Department> departmentJoin = root.join("department", JoinType.INNER);

                predicates.add(cb.like(
                        cb.lower(departmentJoin.get("name")),
                        contains(condition.departmentName())
                ));
            }

            if(hasText(condition.position())) {
                predicates.add(cb.like(
                        cb.lower(root.get("position")),
                        contains(condition.position())
                ));
            }

            if(condition.hireDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("hireDate"),
                        condition.hireDateFrom()
                ));
            }

            if(condition.hireDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("hireDate"),
                        condition.hireDateTo()
                ));
            }

            if(condition.status() != null) {
                predicates.add(cb.equal(
                        root.get("employeeStatus"),
                        condition.status()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String contains(String value) {
        return "%" + value.toLowerCase() + "%";
    }

}
