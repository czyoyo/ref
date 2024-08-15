package com.example.ref.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "survey")
@Comment("설문")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @JoinColumn(name = "survey_category_id", columnDefinition = "BIGINT UNSIGNED", nullable = false, foreignKey = @ForeignKey(name = "FK_SURVEY_SURVEY_CATEGORY_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("설문 카테고리 인덱스")
    private SurveyCategory surveyCategory;

    @Column(name = "title", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("제목")
    private String title;

    @Column(name = "start_at", nullable = false)
    @Comment("시작일시")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @Column(name = "end_at", nullable = false)
    @Comment("종료일시")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Column(name = "content", columnDefinition = "TEXT")
    @Comment("내용")
    private String content;

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SurveyQuestion> surveyQuestionList;

    @JoinColumn(name = "writer_id", columnDefinition = "BIGINT UNSIGNED")
    @ManyToOne(fetch = FetchType.LAZY)
    private Admin writer;

    public void modifySurvey(Survey survey) {
        this.title = survey.getTitle();
        this.startDateTime = survey.getStartDateTime();
        this.endDateTime = survey.getEndDateTime();
        this.content = survey.getContent();
    }

    public void setSurveyCategory(SurveyCategory surveyCategory) {
        this.surveyCategory = surveyCategory;
    }


}
