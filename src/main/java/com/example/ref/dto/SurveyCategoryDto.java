package com.example.ref.dto;

import com.example.ref.entity.SurveyCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyCategoryDto {

    @Schema(description = "설문 카테고리 인덱스", example = "1")
    private Long id;

    @Schema(description = "설문 카테고리명", example = "설문 카테고리명")
    private String title;

    @Schema(description = "분류", example = "분류")
    private String classification;

    @Schema(description = "설명", example = "설명")
    private String memo;

    @Schema(description = "생성일", example = "2021-01-01 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    private static SurveyCategoryDto.SurveyCategoryDtoBuilder surveyCategoryDtoBuilder(SurveyCategory surveyCategory){
        return SurveyCategoryDto.builder()
            .id(surveyCategory.getId())
            .title(surveyCategory.getTitle())
            .classification(surveyCategory.getClassification())
            .memo(surveyCategory.getMemo())
            .createdAt(surveyCategory.getCreatedAt())
            ;
    }

    public static SurveyCategoryDto convertToDto(SurveyCategory surveyCategory) {
        SurveyCategoryDtoBuilder surveyCategoryDtoBuilder = surveyCategoryDtoBuilder(surveyCategory);
        return surveyCategoryDtoBuilder.build();
    }

}
