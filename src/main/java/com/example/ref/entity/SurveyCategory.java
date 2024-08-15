package com.example.ref.entity;

import com.example.ref.request.admin.category.survey.ModifySurveyCategoryRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "survey_category")
@Comment("설문 카테고리")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    @Comment("카테고리명")
    private String title;

    @Column(name="classification", columnDefinition = "VARCHAR(255)")
    @Comment("분류")
    private String classification;

    @Column(name = "memo", columnDefinition = "TEXT")
    @Comment("설명")
    private String memo;

    @OneToMany(mappedBy = "surveyCategory", fetch = FetchType.LAZY)
    @Comment("설문 목록 연관관계")
    private List<Survey> surveyList;


    public void modifySurveyCategory(ModifySurveyCategoryRequestDto modifySurveyCategoryRequestDto) {
        this.title = modifySurveyCategoryRequestDto.getTitle();
        this.memo = modifySurveyCategoryRequestDto.getMemo();
        this.classification = modifySurveyCategoryRequestDto.getClassification();
    }

}
