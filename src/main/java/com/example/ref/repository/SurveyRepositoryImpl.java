package com.example.ref.repository;

import com.example.ref.entity.Survey;
import com.example.ref.rules.SurveyType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
public class SurveyRepositoryImpl implements SurveyCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Survey> findAllByContaining(Pageable pageable, String keyword,
        LocalDateTime startDateTime, LocalDateTime endDateTime, String status) {
        QSurveyLabel subQueryLabel = new QSurveyLabel("subQueryLabel");
        List<Survey> surveyList = queryFactory
            .selectFrom(QSurvey.survey)
            .innerJoin(QSurvey.survey.surveyCategory, QSurveyCategory.surveyCategory).fetchJoin()
            .leftJoin(QSurvey.survey.surveyQuestionList, QSurveyQuestion.surveyQuestion).fetchJoin()
            .leftJoin(subQueryLabel)
            .on(subQueryLabel.surveyQuestion.in(
                JPAExpressions
                    .select(QSurveyQuestion.surveyQuestion)
                    .from(QSurveyQuestion.surveyQuestion)
                    .where(QSurveyQuestion.surveyQuestion.survey.eq(QSurvey.survey))
            ))
            .where(
                searchCondition(keyword, startDateTime, endDateTime, status)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getOrderSpecifiers(pageable))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(QSurvey.survey.count())
            .from(QSurvey.survey)
            .where(searchCondition(keyword, startDateTime, endDateTime, status));

        return PageableExecutionUtils.getPage(surveyList, pageable, countQuery::fetchOne);

    }

    // status true: 진행중, false: 종료, null: 전체

    private BooleanBuilder searchCondition(String keyword, LocalDateTime startDate, LocalDateTime endDate, String status) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(keyword)) {
            builder
                .or(contentContains(keyword))
                .or(titleContains(keyword));
        }
        if (startDate != null && endDate != null) {
            builder.and(startAndEndDateBetween(startDate, endDate));
        }
        if (status != null) {
            LocalDateTime now = LocalDateTime.now();
            if (status.equals(SurveyType.PROGRESS.getType())) {
                // loe: less than or equal
                // goe: greater than or equal
                builder.and(QSurvey.survey.startDateTime.loe(now).and(QSurvey.survey.endDateTime.goe(now)));
            } else if(status.equals(SurveyType.END.getType())) {
                // lt: less than
                builder.and(QSurvey.survey.endDateTime.lt(now));
            } else if(status.equals(SurveyType.WAIT.getType())) {
                // gt: greater than
                builder.and(QSurvey.survey.startDateTime.gt(now));
            }
        }
        return builder;
    }


    private BooleanExpression contentContains(String keyword) {
        return StringUtils.hasText(keyword) ? QSurvey.survey.content.contains(keyword) : null;
    }
    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword) ? QSurvey.survey.title.contains(keyword) : null;
    }
    private BooleanExpression startAndEndDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? QSurvey.survey.createdAt.between(startDate, endDate) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isUnsorted() ? new OrderSpecifier[]{QSurvey.survey.createdAt.desc()} :
            pageable.getSort().stream()
                .map(order -> {
                    if (order.isAscending()) {
                        // 여러가지가 올 수 있다.
                        return switch (order.getProperty()) {
                            case "id" -> QSurvey.survey.id.asc();
                            default -> QSurvey.survey.createdAt.asc();
                        };
                    } else {
                        return switch (order.getProperty()) {
                            case "id" -> QSurvey.survey.id.desc();
                            default -> QSurvey.survey.createdAt.desc();
                        };
                    }
                }).toArray(OrderSpecifier[]::new);
    }



}
