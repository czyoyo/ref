package com.example.ref.repository;

import com.example.ref.entity.Admin;
import com.example.ref.entity.User;
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
public class AdminRepositoryImpl implements AdminCustomRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Admin> findByAdminAndClassificationWithAdminImageList(Pageable pageable,
        List<String> classification, User user) {

        List<Admin> adminList = queryFactory
            .selectFrom(QAdmin.admin)
            .where(QAdmin.admin.userCategory.classification.in(classification))
            .leftJoin(QAdmin.admin.adminImageList).fetchJoin()
            .leftJoin(QAdmin.admin.adminProfileImage).fetchJoin()
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QAdmin.admin.count())
            .from(QAdmin.admin)
            .where(QAdmin.admin.userCategory.classification.in(classification));

        return PageableExecutionUtils.getPage(adminList, pageable, countQuery::fetchOne);
    }


    @Override
    public Page<Admin> finaAllByNicknameContainingAndCreatedAtBetween(Pageable pageable,
        String keyword, LocalDateTime startDate, LocalDateTime endDate, Long userCategoryId, Long userCategoryParentId) {

        List<Admin> adminList = queryFactory
            .selectFrom(QAdmin.admin)
            .where(
                searchCondition(keyword, userCategoryId, userCategoryParentId, startDate, endDate),
                // depth 2 이상만 검색
                QAdmin.admin.userCategory.depth.gt(UserCategoryDepth.MIDDLE_DEPTH.getDepth())

            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();


        JPAQuery<Long> countQuery = queryFactory
            .select(QAdmin.admin.count())
            .from(QAdmin.admin)
            .where(
                searchCondition(keyword, userCategoryId, userCategoryParentId, startDate, endDate),
                // depth 2 이상만 검색
                QAdmin.admin.userCategory.depth.gt(UserCategoryDepth.MIDDLE_DEPTH.getDepth())
            )
            ;

        return PageableExecutionUtils.getPage(adminList, pageable, countQuery::fetchOne);
    }


    @Override
    public Page<Admin> findMyClientAdmin(Pageable pageable, Long adminId) {

        List<Admin> adminList = queryFactory
            .selectFrom(QAdmin.admin)
            .where(QAdmin.admin.id.eq(adminId))
            .leftJoin(QAdmin.admin.adminProfileImage).fetchJoin()
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QAdmin.admin.count())
            .from(QAdmin.admin)
            .where(QAdmin.admin.id.eq(adminId));

        return PageableExecutionUtils.getPage(adminList, pageable, countQuery::fetchOne);
    }



    private BooleanBuilder searchCondition(String keyword, Long userCategoryId, Long userCategoryParentId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(keyword)) {
            builder.or(adminId(keyword));
            builder.or(memoContains(keyword));
            builder.or(nicknameContains(keyword));
        }
        if (userCategoryId != null) {
            builder.and(adminCategoryEq(userCategoryId));
        }
        if (userCategoryParentId != null) {
            builder.and(adminCategoryParentEq(userCategoryParentId));
        }
        if (startDateTime != null && endDateTime != null) {
            builder.and(startAndEndDateBetween(startDateTime, endDateTime));
        }
        return builder;
    }



    private BooleanExpression adminCategoryEq(Long userCategoryId) {
        return userCategoryId != null ? QAdmin.admin.userCategory.id.eq(userCategoryId) : null;
    }
    private BooleanExpression adminCategoryParentEq(Long userCategoryParentId) {
        return userCategoryParentId != null ? QAdmin.admin.userCategory.parent.id.eq(userCategoryParentId) : null;
    }
    private BooleanExpression adminId(String searchText) {
        return StringUtils.hasText(searchText) ? QAdmin.admin.adminId.eq(searchText) : null;
    }
    private BooleanExpression memoContains(String searchText) {
        return StringUtils.hasText(searchText) ? QAdmin.admin.memo.contains(searchText) : null;
    }
    private BooleanExpression nicknameContains(String searchText) {
        return StringUtils.hasText(searchText) ? QAdmin.admin.nickname.contains(searchText) : null;
    }
    private BooleanExpression startAndEndDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? QAdmin.admin.createdAt.between(startDate, endDate) : null;
    }



    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isUnsorted() ?
            new OrderSpecifier[]{QAdmin.admin.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if (order.isAscending()) {
                        // 여러가지가 올 수 있다.
                        return switch (order.getProperty()) {
                            case "id" -> QAdmin.admin.id.asc();
                            default -> QAdmin.admin.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "id" -> QAdmin.admin.id.desc();
                            default -> QAdmin.admin.createdAt.desc();
                        };
                    }
                }).toArray(OrderSpecifier[]::new);
    }






}
