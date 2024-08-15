package com.example.ref.request.admin.survey.survey;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class GetSurveyListRequestDto {

    @Schema(description = "검색 키워드", example = "검색 키워드")
    private String keyword;

    @Schema(description = "진행상태", example = "진행상태 true / false / null  ,true: 진행중, false: 종료, null: 전체")
    private String searchStatus;

    @Schema(description = "시작일", example = "2021-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2021-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

}
