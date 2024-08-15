package com.example.ref.entity;

import com.example.ref.request.admin.survey.question.ModifySurveyQuestionRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "survey_question")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestion extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("인덱스")
    private long id;

    @Column(name = "question", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("설문지 질문")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", columnDefinition = "BIGINT UNSIGNED")
    private Survey survey;

    @OneToMany(mappedBy = "surveyQuestion", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SurveyLabel> surveyLabelList;

    @Column(name = "type", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("설문지 질문 타입")
    private String type;

    public void modifySurveyQuestion(ModifySurveyQuestionRequestDto modifySurveyQuestionRequestDto) {
        this.title = modifySurveyQuestionRequestDto.getTitle();
    }

    // 편의 메소드
    public void setSurvey(Survey survey) {
        if(this.survey != null) {
            this.survey.getSurveyQuestionList().remove(this);
        }
        this.survey = survey;
        survey.getSurveyQuestionList().add(this);
    }


}
