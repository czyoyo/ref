package com.example.ref.request.admin.survey.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifySurveyQuestionRequestDto {

    @NotNull(message = "잘못된 요청입니다. 설문조사가 선택되지 않았습니다.")
    @Schema(description = "설문조사 질문 인덱스", example = "1")
    private Long surveyQuestionIdx;

    @NotBlank(message = "잘못된 요청입니다. 설문조사 질문의 제목이 입력되지 않았습니다.")
    @Schema(description = "질문 제목", example = "질문 제목 입니다.")
    private String title;

}
