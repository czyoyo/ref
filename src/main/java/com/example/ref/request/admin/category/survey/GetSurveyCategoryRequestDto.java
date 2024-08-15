package com.example.ref.request.admin.category.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSurveyCategoryRequestDto {

    @Schema(description = "검색어", example = "검색어", nullable = true)
    private String keyword;

    @Schema(description = "시작일", example = "2021-01-01", nullable = true)
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2021-12-31", nullable = true)
    private LocalDate endDate;

    GetSurveyCategoryRequestDto() {
        this.keyword = "";
        this.startDate = null;
        this.endDate = null;
    }
}
