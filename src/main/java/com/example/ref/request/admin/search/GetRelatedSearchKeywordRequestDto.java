package com.example.ref.request.admin.search;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetRelatedSearchKeywordRequestDto {

    @Schema(description = "검색어", example = "배구공")
    @NotBlank(message = "검색어를 입력해주세요.")
    private String searchKeyword;

}
