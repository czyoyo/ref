package com.example.ref.request.admin.survey.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AddSurveyQuestionRequestDto {

    @NotNull(message = "잘못된 요청입니다. 설문조사가 선택되지 않았습니다.")
    @Schema(description = "설문조사 인덱스", example = "1")
    private Long surveyIdx;

    @NotNull(message = "잘못된 요청입니다. 설문조사 질문의 제목이 입력되지 않았습니다.")
    @Schema(description = "질문 제목", example = "질문 제목 입니다.")
    private String title;

    @NotNull(message = "잘못된 요청입니다. 설문조사 질문의 타입이 입력되지 않았습니다.")
    @Schema(description = "질문 타입", example = "text")
    private String type;
}
