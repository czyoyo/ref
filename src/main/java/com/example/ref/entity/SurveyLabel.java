package com.example.ref.entity;

import com.example.ref.request.admin.survey.label.ModifySurveyLabelRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "survey_label")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyLabel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(255)", nullable = false)
    private String title;

    @Column(name = "is_check", columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id", columnDefinition = "BIGINT UNSIGNED")
    private SurveyQuestion surveyQuestion;

    public void modifySurveyLabel(ModifySurveyLabelRequestDto modifySurveyLabelRequestDto) {
        this.title = modifySurveyLabelRequestDto.getTitle();
    }

    // 편의 메소드
    public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
        if(this.surveyQuestion != null) {
            this.surveyQuestion.getSurveyLabelList().remove(this);
        }
        this.surveyQuestion = surveyQuestion;
        surveyQuestion.getSurveyLabelList().add(this);
    }

}
