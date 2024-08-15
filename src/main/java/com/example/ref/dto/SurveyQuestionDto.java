package com.example.ref.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
public class SurveyQuestionDto {

    @Schema(description = "설문 질문 인덱스", example = "1")
    private Long id;

    @Schema(description = "설문 질문명", example = "이러이러한 건 무엇 인가요?")
    private String title;

    @Schema(description = "설문 타입", example = "check")
    private String type;

    @Schema(description = "설문", example = "설문 정보")
    private SurveyDto survey;

    @Schema(description = "라벨 리스트", example = "라벨 리스트 정보")
    private List<SurveyLabelDto> surveyLabelList;

    @Schema(description = "설문 질문 카테고리 정보")
    private SurveyQuestionCategoryDto surveyQuestionCategory;

}
