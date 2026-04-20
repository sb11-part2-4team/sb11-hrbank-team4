package com.sb11.hr_bank.domain.employee.repository;

import com.sb11.hr_bank.domain.department.entity.Department;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCountCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeCursor;
import com.sb11.hr_bank.domain.employee.dto.EmployeeSearchCondition;
import com.sb11.hr_bank.domain.employee.entity.Employee;
import com.sb11.hr_bank.global.exception.BusinessException;
import com.sb11.hr_bank.global.exception.ErrorCode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecifications {

    public static Specification<Employee> searchCondition(EmployeeSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("department", JoinType.LEFT);
            }

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

    public static Specification<Employee> cursorCondition(EmployeeCursor cursor) {
        return (root, query, cb) -> {
            if(cursor == null || cursor.value() == null || cursor.idAfter() == null) {
                return cb.conjunction();
            }

            boolean desc = "DESC".equalsIgnoreCase(cursor.sortDirection());
            Path<Long> idPath = root.get("id");

            return switch (cursor.sortField()) {
                case "name", "employeeNumber" -> {
                    Path<String> path = root.get(cursor.sortField());
                    yield cursorPredicate(cb, path, cursor.value(), idPath, cursor.idAfter(), desc);
                }
                case "hireDate" -> {
                    Path<LocalDate> path = root.get("hireDate");
                    yield cursorPredicate(cb, path, LocalDate.parse(cursor.value()), idPath, cursor.idAfter(), desc);
                }
                default -> throw new BusinessException(ErrorCode.EMPLOYEE_INVALID_SORT_FIELD);
            };
        };
    }

    public static Specification<Employee> countCondition(EmployeeCountCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(condition == null) {
                return cb.conjunction();
            }

            if(condition.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("hireDate"),
                        condition.fromDate()
                ));
            }

            if(condition.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("hireDate"),
                        condition.toDate()
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
        return "%" + value.trim().toLowerCase() + "%";
    }

    private static <T extends Comparable<? super T>> Predicate cursorPredicate(
            CriteriaBuilder cb,
            Path<T> sortPath,
            T cursorValue,
            Path<Long> idPath,
            Long idAfter,
            boolean desc
    ) {
        Predicate sortPredicate = desc
                ? cb.lessThan(sortPath, cursorValue)
                : cb.greaterThan(sortPath, cursorValue);

        Predicate idPredicate= desc
                ? cb.and(cb.equal(sortPath, cursorValue), cb.lessThan(idPath, idAfter))
                : cb.and(cb.equal(sortPath, cursorValue), cb.greaterThan(idPath, idAfter));

        return cb.or(sortPredicate, idPredicate);
    }

}
