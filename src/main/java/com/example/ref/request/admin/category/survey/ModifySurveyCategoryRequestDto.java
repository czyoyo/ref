package com.example.ref.request.admin.category.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifySurveyCategoryRequestDto {

    @NotNull(message = "카테고리 식별자가 없습니다.")
    @Schema(description = "설문 카테고리 인덱스", example = "1")
    private Long surveyCategoryIdx;

    @Schema(description = "분류", example = "GOOD_SURVEY_1", nullable = true)
    private String classification;

    @NotBlank(message = "권한명은 필수 입력값입니다.")
    @Schema(description = "권한명", example = "권한명")
    private String title;

    @Schema(description = "설명", example = "설명", nullable = true)
    private String memo;

}
