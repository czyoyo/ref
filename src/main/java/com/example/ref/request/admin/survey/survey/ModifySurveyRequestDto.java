package com.example.ref.request.admin.survey.survey;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifySurveyRequestDto {

    @NotNull(message = "필수값이 없습니다.")
    @Schema(description = "설문조사 인덱스", example = "1")
    private Long surveyIdx;

    @NotNull(message = "카테고리를 선택해주세요.")
    @Schema(description = "설문조사 카테고리 인덱스", example = "1")
    private Long surveyCategoryIdx;

    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(description = "제목", example = "제목입니다.")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "시작일시를 입력해주세요.")
    @Schema(description = "시작일시", example = "2021-01-01 00:00:00")
    private LocalDateTime startDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "종료일시를 입력해주세요.")
    @Schema(description = "종료일시", example = "2021-01-01 00:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "내용", example = "내용입니다.")
    @NotNull(message = "내용을 입력해주세요.(공백이라도)")
    private String content;

}
