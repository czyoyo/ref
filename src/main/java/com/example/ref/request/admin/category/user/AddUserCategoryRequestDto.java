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
public class AddUserCategoryRequestDto {

    @Schema(description = "부모 카테고리 인덱스", example = "1", nullable = true)
    @NotNull(message = "부모 카테고리 인덱스를 입력해주세요.")
    private Long userCategoryParentIdx;

    @NotBlank(message = "카테고리명을 입력해주세요.")
    @Schema(description = "카테고리명", example = "카테고리명")
    private String title;

    @Schema(description = "메모", example = "메모", nullable = true)
    private String memo;

}
