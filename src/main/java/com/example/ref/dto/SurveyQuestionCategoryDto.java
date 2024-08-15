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
public class SurveyQuestionCategoryDto {

    @Schema(description = "설문 질문 카테고리 인덱스", example = "1")
    private Long id;

    @Schema(description = "설문 질문 카테고리명", example = "설문 질문 카테고리명")
    private String title;

    @Schema(description = "분류", example = "분류")
    private String classification;

    @Schema(description = "설명", example = "설명")
    private String memo;

    @Schema(description = "부모 설문 질문 카테고리 인덱스", example = "1")
    private Long parentId;

    @Schema(description = "뎁스", example = "1")
    private Integer depth;

}
