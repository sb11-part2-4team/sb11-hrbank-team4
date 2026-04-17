package com.sb11.hr_bank.domain.employee.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb11.hr_bank.domain.department.entity.QDepartment;
import com.sb11.hr_bank.domain.employee.dto.EmployeeDistributionRow;
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
    public List<EmployeeDistributionRow> findDistribution(String groupBy, EmployeeStatus status) {

        QEmployee employee = QEmployee.employee;
        QDepartment department = QDepartment.department;

        StringExpression groupExpression = switch (groupBy) {
            case "department" -> department.name;
            case "position" -> employee.position;
            default -> throw new IllegalArgumentException("groupBy는 department 또는 position만 가능합니다.");
        };

        NumberExpression<Long> countExpression = employee.count();

        return queryFactory
                .select(Projections.constructor(
                        EmployeeDistributionRow.class,
                        groupExpression,
                        countExpression
                ))
                .from(employee)
                .join(employee.department, department)
                .where(employee.employeeStatus.eq(status))
                .groupBy(groupExpression)
                .fetch();

    }

}
