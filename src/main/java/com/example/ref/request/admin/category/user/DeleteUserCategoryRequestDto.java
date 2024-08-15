package com.example.ref.request.admin.category.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DeleteUserCategoryRequestDto {

    @NotBlank(message = "카테고리를 선택해주세요.")
    @Schema(description = "사용자 카테고리 인덱스", example = "1")
    private Long userCategoryIdx;

}
