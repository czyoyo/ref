package com.example.ref.request.admin.search;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddSearchKeywordRequestDto {

    @NotBlank(message = "검색어가 없습니다.")
    @Schema(description = "검색어", example = "배구공")
    private String searchKeyword;

}
