package com.example.ref.request.admin.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetAdminRequestDto {

    @Schema(description = "검색 키워드", example = "검색 키워드")
    private String keyword;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "시작일", example = "2021-01-01")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "종료일", example = "2021-12-01")
    private LocalDate endDate;

    @Schema(description = "유저 카테고리 부모 아이디(권한)", example = "1")
    private Long userCategoryParentIdx;

    @Schema(description = "유저 카테고리 아이디(카테고리)", example = "1")
    private Long userCategoryIdx;

}
