package com.example.ref.repository;

import com.example.ref.entity.BoardNotice;
import com.example.ref.entity.QBoardNotice;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class BoardNoticeRepositoryImpl implements BoardNoticeCustomRepository {

    private final JPAQueryFactory queryFactory;


    public Page<BoardNotice> findAllByTitleContainingAndCreatedAtBetween(Pageable pageable, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<BoardNotice> boardNoticeList = queryFactory
            .selectFrom(QBoardNotice.boardNotice)
            .where(titleContains(keyword), startAndEndDateBetween(startDateTime, endDateTime))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QBoardNotice.boardNotice.count())
            .from(QBoardNotice.boardNotice)
            .where(titleContains(keyword), startAndEndDateBetween(startDateTime, endDateTime));

        return PageableExecutionUtils.getPage(boardNoticeList, pageable, countQuery::fetchOne);

    }



    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? QBoardNotice.boardNotice.title.contains(title) : null;
    }

    private BooleanExpression startAndEndDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime != null && endDateTime != null ? QBoardNotice.boardNotice.createdAt.between(startDateTime, endDateTime) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isUnsorted() ? new OrderSpecifier[]{QBoardNotice.boardNotice.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if(order.isAscending()) {
                        return switch (order.getProperty()) {
                            case "title" -> QBoardNotice.boardNotice.title.asc();
                            default -> QBoardNotice.boardNotice.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "title" -> QBoardNotice.boardNotice.title.desc();
                            default -> QBoardNotice.boardNotice.createdAt.desc();
                        };
                    }
                }).toArray(OrderSpecifier[]::new);
    }




}
