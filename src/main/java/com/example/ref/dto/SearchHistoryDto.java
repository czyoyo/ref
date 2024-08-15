package com.example.ref.dto;

import com.example.ref.entity.SearchHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryDto {
    @Schema(description = "인덱스")
    private Long id;
    @Schema(description = "검색어")
    private String searchWord;
    @Schema(description = "검색 횟수")
    private Long searchCount;

    public static SearchHistoryDto convertEntityToDto(SearchHistory searchHistory) {
        return SearchHistoryDto.builder()
            .id(searchHistory.getId())
            .searchWord(searchHistory.getSearchWord())
            .searchCount(searchHistory.getSearchCount())
            .build();
    }

}
