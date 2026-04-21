package com.sb11.hr_bank.domain.backup.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb11.hr_bank.domain.backup.dto.BackupSearchCondition;
import com.sb11.hr_bank.domain.backup.entity.Backup;
import com.sb11.hr_bank.domain.backup.entity.BackupStatus;
import com.sb11.hr_bank.domain.backup.entity.QBackup;
import com.sb11.hr_bank.domain.backup.query.BackupSortDirection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QBackup b = QBackup.backup;

  @Override
  public Slice<Backup> search(BackupSearchCondition condition) {

    List<OrderSpecifier<?>> orders = createOrder(condition);

    List<Backup> result = queryFactory.selectFrom(b)
        .where(
            workerLike(condition.worker()),
            statusEq(condition.status()),
            startBetween(condition.startFrom(), condition.startTo()),
            applyCursor(condition)
        )
        .orderBy(orders.toArray(new OrderSpecifier[0]))
        .limit(condition.size() + 1)
        .fetch();

    boolean hasNext = result.size() > condition.size();

    if (hasNext) {
      result.remove(result.size() - 1);
    }

    return new SliceImpl<>(result, PageRequest.of(0, condition.size()), hasNext);

  }


  // 정렬
  private List<OrderSpecifier<?>> createOrder(BackupSearchCondition condition) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    Order direction = condition.sortDirection() == BackupSortDirection.ASC
        ? Order.ASC : Order.DESC;

    switch (condition.sortField()) {

      case STARTED_AT -> {
        orders.add(new OrderSpecifier<>(direction, b.startedAt));
        orders.add(new OrderSpecifier<>(direction, b.id));
      }

      case ENDED_AT -> {
        orders.add(new OrderSpecifier<>(direction, b.endedAt));
        orders.add(new OrderSpecifier<>(direction, b.id));
      }

      case STATUS -> {
        orders.add(new OrderSpecifier<>(direction, b.status));
        orders.add(new OrderSpecifier<>(direction, b.id));
      }
    }

    return orders;

  }

  // 커서
  private BooleanExpression applyCursor(BackupSearchCondition condition) {
    if (condition.cursorId() == null) {
      return null;
    }

    boolean isAsc = condition.sortDirection() == BackupSortDirection.ASC;

    return switch (condition.sortField()) {

      case STARTED_AT -> isAsc
          ? b.startedAt.gt(condition.cursorStartedAt())
          .or
              (b.startedAt.eq(condition.cursorStartedAt())
                  .and(b.id.gt(condition.cursorId())))
          : b.startedAt.lt(condition.cursorStartedAt())
              .or
                  (b.startedAt.eq(condition.cursorStartedAt())
                      .and(b.id.lt(condition.cursorId())));

      case ENDED_AT -> isAsc
          ? b.endedAt.gt(condition.cursorEndedAt())
          .or
              (b.endedAt.eq(condition.cursorEndedAt())
                  .and(b.id.gt(condition.cursorId())))
          : b.endedAt.lt(condition.cursorEndedAt())
              .or
                  (b.endedAt.eq(condition.cursorEndedAt())
                      .and(b.id.lt(condition.cursorId())));

      case STATUS -> isAsc
          ? b.status.gt(condition.cursorStatus())
          .or
              (b.status.eq(condition.cursorStatus())
                  .and(b.id.gt(condition.cursorId())))
          : b.status.lt(condition.cursorStatus())
              .or
                  (b.status.eq(condition.cursorStatus())
                      .and(b.id.lt(condition.cursorId())));
    };
  }

  // where조건
  private BooleanExpression workerLike(String worker) {
    return worker == null ? null : b.worker.contains(worker);
  }

  private BooleanExpression statusEq(Enum<?> status) {
    return status == null ? null : b.status.eq((BackupStatus) status);
  }

  private BooleanExpression startBetween(Instant from, Instant to) {
    if (from != null && to != null) {
      return b.startedAt.between(from, to);
    }
    if (from != null) {
      return b.startedAt.goe(from);
    }
    if (to != null) {
      return b.startedAt.loe(to);
    }
    return null;
  }
}