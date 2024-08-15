package com.example.ref.request.admin.category.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AddSurveyCategoryRequestDto {

    @NotBlank(message = "구분명은 필수 입력값입니다.")
    @Schema(description = "구분명", example = "구분명")
    private String title;

    @Schema(description = "설명", example = "설명", nullable = true)
    private String memo;

    @Schema(description = "부모 설문 카테고리 인덱스", example = "1", nullable = true)
    private Long surveyCategoryParentIdx;

    @Schema(description = "분류", example = "GOOD_SURVEY_1", nullable = true)
    private String classification;


}
