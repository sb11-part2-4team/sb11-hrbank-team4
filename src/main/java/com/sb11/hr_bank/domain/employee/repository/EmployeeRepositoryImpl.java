package com.sb11.hr_bank.domain.employee.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb11.hr_bank.domain.department.entity.QDepartment;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionCondition;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionDto;
import com.sb11.hr_bank.domain.employee.entity.EmployeeStatus;
import com.sb11.hr_bank.domain.employee.entity.QEmployee;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public EmployeeRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<EmployeeDistributionDto> findDistribution(EmployeeDistributionCondition condition) {

        QEmployee employee = QEmployee.employee;
        QDepartment department = QDepartment.department;

        String groupBy = condition != null && condition.groupBy() != null && !condition.groupBy().isBlank()
                ? condition.groupBy()
                : "department";

        EmployeeStatus status = condition != null && condition.status() != null
                ? condition.status()
                : EmployeeStatus.ACTIVE;

        StringExpression groupExpression = switch (groupBy) {
            case "department" -> department.name;
            case "position" -> employee.position;
            default -> throw new IllegalArgumentException("groupBy는 department 또는 position만 가능합니다.");
        };

        NumberExpression<Long> countExpression = employee.count();

        List<Tuple> rows = queryFactory
                .select(groupExpression, countExpression)
                .from(employee)
                .join(employee.department, department)
                .where(employee.employeeStatus.eq(status))
                .groupBy(groupExpression)
                .fetch();

        long total = rows.stream()
                .mapToLong(row -> row.get(countExpression))
                .sum();

        return rows.stream()
                .map(row -> {
                    Long count = row.get(countExpression);
                    return new EmployeeDistributionDto(
                            row.get(groupExpression),
                            count,
                            total == 0 ? 0.0 : Math.round(count * 1000.0 / total) / 10.0
                    );
                })
                .toList();

    }

}
