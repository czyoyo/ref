package com.example.ref.repository;

import com.example.ref.entity.NoticeCategory;
import com.querydsl.core.BooleanBuilder;
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
public class NoticeCategoryRepositoryImpl implements NoticeCategoryCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<NoticeCategory> findAllByTitleContainingAndCreatedAtBetweenStartDateTimeAndEndDateTime(Pageable pageable, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<NoticeCategory> noticeCategoryList = queryFactory
            .selectFrom(QNoticeCategory.noticeCategory)
            .where(getSearchCondition(keyword, startDateTime, endDateTime))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QNoticeCategory.noticeCategory.count())
            .from(QNoticeCategory.noticeCategory)
            .where(getSearchCondition(keyword, startDateTime, endDateTime));

        return PageableExecutionUtils.getPage(noticeCategoryList, pageable, countQuery::fetchOne);

    }

    private BooleanBuilder getSearchCondition(String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            builder
                .or(titleContains(keyword))
                .or(memoContains(keyword));
        }
        if (startDateTime != null && endDateTime != null) {
            builder.and(startAndEndDateBetween(startDateTime, endDateTime));
        }
        return builder;
    }


    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword) ? QNoticeCategory.noticeCategory.title.contains(keyword) : null;
    }
    private BooleanExpression memoContains(String keyword) {
        return StringUtils.hasText(keyword) ? QNoticeCategory.noticeCategory.memo.contains(keyword) : null;
    }
    private BooleanExpression startAndEndDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime != null && endDateTime != null ? QNoticeCategory.noticeCategory.createdAt.between(startDateTime, endDateTime) : null;
    }


    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {

        return pageable.getSort().isUnsorted() ? new OrderSpecifier[] {QNoticeCategory.noticeCategory.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if (order.isAscending()) {
                        return switch (order.getProperty()) {
                            case "id" -> QNoticeCategory.noticeCategory.id.asc();
                            default -> QNoticeCategory.noticeCategory.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "id" -> QNoticeCategory.noticeCategory.id.desc();
                            default -> QNoticeCategory.noticeCategory.createdAt.desc();
                        };
                    }
                }).toArray(OrderSpecifier[]::new);
    }


}
