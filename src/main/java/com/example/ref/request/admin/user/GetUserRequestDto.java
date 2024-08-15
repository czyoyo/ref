package com.example.ref.request.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetUserRequestDto {

    @Schema(description = "검색어", example = "검색어", nullable = true)
    private String keyword;

    @Schema(description = "시작일", example = "2021-01-01", nullable = true)
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2021-12-31", nullable = true)
    private LocalDate endDate;

    @Schema(description = "유저카테고리 인덱스")
    private Long userCategoryIdx;

    @Schema(description = "유저카테고리 부모 인덱스")
    private Long userCategoryParentIdx;

}
