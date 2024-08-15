package com.example.ref.request.admin.survey.label;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ModifySurveyLabelRequestDto {

    @NotNull(message = "잘못된 요청입니다. 설문조사 라벨이 선택되지 않았습니다.")
    @Schema(description = "설문조사 라벨 인덱스", example = "1")
    private Long surveyLabelIdx;

    @NotNull(message = "잘못된 요청입니다. 설문조사 라벨의 제목이 입력되지 않았습니다.")
    @Schema(description = "라벨 제목", example = "라벨 제목 입니다.")
    private String title;

}
