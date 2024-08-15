package com.example.ref.repository;

import com.example.ref.entity.QSurveyCategory;
import com.example.ref.entity.SurveyCategory;
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
public class SurveyCategoryRepositoryImpl implements SurveyCategoryCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<SurveyCategory> findAllByClassificationAndTitleContainingAndCreatedAtBetween(
        String keyword, LocalDateTime startDate, LocalDateTime endDate,
        Pageable pageable) {

        List<SurveyCategory> surveyCategoryList = queryFactory
            .selectFrom(QSurveyCategory.surveyCategory)
            .where(getSearchCondition(keyword, startDate, endDate))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QSurveyCategory.surveyCategory.count())
            .from(QSurveyCategory.surveyCategory)
            .where(getSearchCondition(keyword, startDate, endDate));

        return PageableExecutionUtils.getPage(surveyCategoryList, pageable, countQuery::fetchOne);
    }



    private BooleanBuilder getSearchCondition(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
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

    private BooleanExpression startAndEndDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? QSurveyCategory.surveyCategory.createdAt.between(startDate, endDate) : null;
    }
    private BooleanExpression memoContains(String keyword) {
        return StringUtils.hasText(keyword) ? QSurveyCategory.surveyCategory.memo.contains(keyword) : null;
    }
    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword) ? QSurveyCategory.surveyCategory.title.contains(keyword) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isUnsorted() ?
            new OrderSpecifier[]{QSurveyCategory.surveyCategory.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if (order.isAscending()) {
                        // 여러가지가 올 수 있다.
                        return switch (order.getProperty()) {
                            case "id" -> QSurveyCategory.surveyCategory.id.asc();
                            default -> QSurveyCategory.surveyCategory.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "id" -> QSurveyCategory.surveyCategory.id.desc();
                            default -> QSurveyCategory.surveyCategory.createdAt.desc();
                        };
                    }
                }).toArray(OrderSpecifier[]::new);
    }


}
