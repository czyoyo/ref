package com.example.ref.repository;

import com.example.ref.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyCustomRepository {

    @Query("select s from Survey s join fetch s.surveyCategory left join fetch s.surveyQuestionList sq left join sq.surveyLabelList sl where s.id = :id")
    Survey findWithExtraById(@Param("id") Long id);

}
