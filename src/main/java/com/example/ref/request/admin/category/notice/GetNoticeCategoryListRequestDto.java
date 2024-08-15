package com.example.ref.request.admin.category.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetNoticeCategoryListRequestDto {

    @Schema(description = "검색 키워드", example = "검색 키워드", nullable = true)
    private String keyword;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "검색 시작 일", example = "2021-07-01")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "검색 종료 일", example = "2021-07-01")
    private LocalDate endDate;



}
