package com.example.ref.request.admin.category.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AddNoticeCategoryRequestDto {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    @Schema(description = "카테고리명", example = "카테고리명")
    private String title;

    @Schema(description = "메모", example = "메모")
    private String memo;

}
