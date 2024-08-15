package com.example.ref.request.admin.category.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ModifyNoticeCategoryRequestDto {

    @NotNull(message = "필수값이 없습니다.")
    @Schema(description = "공지사항 카테고리 인덱스", example = "1")
    private Long noticeCategoryIdx;

    @Schema(description = "카테고리명", example = "카테고리명")
    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String title;

    @Schema(description = "메모", example = "메모")
    private String memo;

}
