package com.example.ref.repository;


import com.example.ref.entity.UserCategory;
import com.example.ref.rules.UserCategoryDepth;
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
public class UserCategoryRepositoryImpl implements UserCategoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserCategory> findAllByClassificationAndTitleContainingAndCreatedAtBetween(
        String keyword, LocalDateTime startDate, LocalDateTime endDate,
        Pageable pageable) {

        List<UserCategory> userCategoryList = queryFactory
            .selectFrom(QUserCategory.userCategory)
            .where(
                searchCondition(keyword, startDate, endDate),
                // depth 2이상만
                QUserCategory.userCategory.depth.gt(UserCategoryDepth.MIDDLE_DEPTH.getDepth())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QUserCategory.userCategory.count())
            .from(QUserCategory.userCategory)
            .where(
                searchCondition(keyword, startDate, endDate),
                // depth 2이상만
                QUserCategory.userCategory.depth.gt(UserCategoryDepth.MIDDLE_DEPTH.getDepth())
            );

        return PageableExecutionUtils.getPage(userCategoryList, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isUnsorted() ?
            new OrderSpecifier[]{QUserCategory.userCategory.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if (order.isAscending()) {
                        // 여러가지가 올 수 있다.
                        return switch (order.getProperty()) {
                            case "id" -> QUserCategory.userCategory.id.asc();
                            default -> QUserCategory.userCategory.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "id" -> QUserCategory.userCategory.id.desc();
                            default -> QUserCategory.userCategory.createdAt.desc();
                        };
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }

    // or 과 and 로 묶기
    private BooleanBuilder searchCondition(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            builder
                .or(titleContains(keyword))
                .or(memoContains(keyword));
        }
        if (startDate != null && endDate != null) {
            builder.and(startAndEndDateBetween(startDate, endDate));
        }
        return builder;
    }


    // 메모 검색
    private BooleanExpression memoContains(String keyword) {
        return StringUtils.hasText(keyword) ? QUserCategory.userCategory.memo.contains(keyword) : null;
    }
    // 날짜 범위 검색
    private BooleanExpression startAndEndDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? QUserCategory.userCategory.createdAt.between(startDate, endDate) : null;
    }
    // 타이틀 검색
    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword) ? QUserCategory.userCategory.title.contains(keyword) : null;
    }



}
