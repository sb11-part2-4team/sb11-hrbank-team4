package com.sb11.hr_bank.domain.backup.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb11.hr_bank.domain.backup.dto.BackupCursor;
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
  public Slice<Backup> search(BackupSearchCondition condition, BackupCursor cursor) {

    // 정렬 조건을 생성(정렬 기준 - 시작시간, 종료시간, 상태 / 정렬 방향 - ASC, DESC)
    List<OrderSpecifier<?>> orders = createOrder(condition);

    // cursor 조건을 적용하여 QueryDSL을 사용하여 조회를 수행, size+1만큼 조회하여 다음 페이지가 존재하는지 여부를 판단
    List<Backup> result = queryFactory.selectFrom(b)
        .where(
            workerLike(condition.worker()),
            statusEq(condition.status()),
            startBetween(condition.startedAtFrom(), condition.startedAtTo()),
            applyCursor(condition, cursor)
        )
        .orderBy(orders.toArray(new OrderSpecifier[0]))
        .limit(condition.size() + 1)
        .fetch();

    // 다음 페이지가 존재하는지 여부를 판단
    boolean hasNext = result.size() > condition.size();

    // size+1만큼 조회한 결과에서 마지막 데이터는 다음 페이지 존재 여부 판단용 데이터이기 때문에 제거
    if (hasNext) {
      result.remove(result.size() - 1);
    }

    // Slice 형태로 반환
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

  // 이전 페이지의 마지막 데이터를 기준으로 다음 페이지를 가져오기 위한 조건 생성 메서드
  private BooleanExpression applyCursor(BackupSearchCondition condition, BackupCursor cursor) {

    if (cursor == null) {
      return null;
    }

    boolean isAsc = condition.sortDirection() == BackupSortDirection.ASC;

    // 각 정렬 기준별로 정렬방향 결정, 정렬 기준이 같을 경우 id로 정렬
    return switch (condition.sortField()) {

      case STARTED_AT -> isAsc
          ? b.startedAt.gt(cursor.startedAt())
          .or
              (b.startedAt.eq(cursor.startedAt())
                  .and(b.id.lt(cursor.id())))
          : b.startedAt.lt(cursor.startedAt())
              .or
                  (b.startedAt.eq(cursor.startedAt())
                      .and(b.id.lt(cursor.id())));

      case ENDED_AT -> isAsc
          ? b.endedAt.gt(cursor.endedAt())
          .or
              (b.endedAt.eq(cursor.endedAt())
                  .and(b.id.lt(cursor.id())))
          : b.endedAt.lt(cursor.endedAt())
              .or
                  (b.endedAt.eq(cursor.endedAt())
                      .and(b.id.lt(cursor.id())));

      case STATUS -> isAsc
          ? b.status.gt(cursor.status())
          .or
              (b.status.eq(cursor.status())
                  .and(b.id.lt(cursor.id())))
          : b.status.lt(cursor.status())
              .or
                  (b.status.eq(cursor.status())
                      .and(b.id.lt(cursor.id())));
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