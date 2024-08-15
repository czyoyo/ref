package com.example.ref.request.admin.category.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ModifyUserCategoryRequestDto {

    @NotNull(message = "카테고리 식별자가 없습니다.")
    @Schema(description = "사용자 카테고리 인덱스", example = "1")
    private Long userCategoryIdx;

    // 변경 될 카테고리 인덱스
    @NotNull(message = "변경 될 부모 카테고리 인덱스를 입력해주세요.")
    @Schema(description = "변경 될 부모 카테고리 인덱스", example = "1")
    private Long userCategoryParentIdx;

    @NotBlank(message = "권한명은 필수 입력값입니다.")
    @Schema(description = "권한명", example = "권한명")
    private String title;

    @Schema(description = "설명", example = "설명", nullable = true)
    private String memo;

}
