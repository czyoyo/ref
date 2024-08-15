package com.example.ref.repository;

import com.example.ref.entity.QUser;
import com.example.ref.entity.User;
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
public class UserRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> finaAllByNicknameContainingAndCreatedAtBetween(Pageable pageable,
        String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime, Long userCategoryId, Long userCategoryParentId) {

        List<User> userList = queryFactory
            .selectFrom(QUser.user)
            .where(searchCondition(keyword, userCategoryId, userCategoryParentId, startDateTime, endDateTime))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QUser.user.count())
            .from(QUser.user)
            .where(searchCondition(keyword, userCategoryId, userCategoryParentId, startDateTime, endDateTime))
            ;

        return PageableExecutionUtils.getPage(userList, pageable, countQuery::fetchOne);

    }


    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isUnsorted() ?
            new OrderSpecifier[]{QUser.user.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if (order.isAscending()) {
                        // 여러가지가 올 수 있다.
                        return switch (order.getProperty()) {
                            case "id" -> QUser.user.id.asc();
                            default -> QUser.user.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "id" -> QUser.user.id.desc();
                            default -> QUser.user.createdAt.desc();
                        };
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }


    private BooleanBuilder searchCondition(String keyword, Long userCategoryId, Long userCategoryParentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(keyword)) {
            builder.or(userIdEq(keyword));
            builder.or(memoContains(keyword));
            builder.or(nicknameContains(keyword));
            builder.or(adminNicknameContains(keyword));
        }
        if (userCategoryId != null) {
            builder.and(userCategoryEq(userCategoryId));
        }
        if (userCategoryParentId != null) {
            builder.and(userCategoryParentEq(userCategoryParentId));
        }
        if (startDateTime != null && endDateTime != null) {
            builder.and(startAndEndDateBetween(startDateTime, endDateTime));
        }
        return builder;
    }


    // 카테고리 조건 추가
    private BooleanExpression userCategoryEq(Long userCategoryId) {
        return userCategoryId != null ? QUser.user.userCategory.id.eq(userCategoryId) : null;
    }
    // 카테고리 부모 조건 추가
    private BooleanExpression userCategoryParentEq(Long userCategoryParentId) {
        return userCategoryParentId != null ? QUser.user.userCategory.parent.id.eq(userCategoryParentId) : null;
    }
    // 담당어드민 닉네임 검색
    private BooleanExpression adminNicknameContains(String searchText) {
        return StringUtils.hasText(searchText) ? QUser.user.admin.nickname.contains(searchText) : null;
    }
    private BooleanExpression memoContains(String searchText) {
        return StringUtils.hasText(searchText) ? QUser.user.memo.contains(searchText) : null;
    }
    private BooleanExpression userIdEq(String searchText) {
        return StringUtils.hasText(searchText) ? QUser.user.userId.eq(searchText) : null;
    }
    private BooleanExpression nicknameContains(String searchText) {
        return StringUtils.hasText(searchText) ? QUser.user.nickname.contains(searchText) : null;
    }
    private BooleanExpression startAndEndDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? QUser.user.createdAt.between(startDate, endDate) : null;
    }


}
