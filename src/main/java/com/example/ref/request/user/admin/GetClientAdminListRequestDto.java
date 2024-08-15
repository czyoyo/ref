package com.example.ref.request.user.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetClientAdminListRequestDto {

    @Schema(description = "검색 키워드", example = "1", nullable = true)
    private String keyword;
    @Schema(description = "시작일", example = "2021-01-01", nullable = true)
    private LocalDate startDate;
    @Schema(description = "종료일", example = "2021-01-01", nullable = true)
    private LocalDate endDate;


}
