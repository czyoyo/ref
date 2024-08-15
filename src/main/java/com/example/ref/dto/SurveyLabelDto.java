package com.example.ref.dto;


import io.swagger.v3.oas.annotations.media.Schema;
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
public class SurveyLabelDto {

    @Schema(description = "설문 라벨 인덱스", example = "1")
    private Long id;

    @Schema(description = "설문 라벨명", example = "설문 라벨명")
    private String title;

    @Schema(description = "설문 라벨 체크형인지", example = "true")
    private Boolean isCheck;

    @Schema(description = "설문 질문 인덱스", example = "1")
    private SurveyQuestionDto surveyQuestionDto;

}
