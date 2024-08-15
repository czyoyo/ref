package com.example.ref.dto;

import com.example.ref.entity.NoticeCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCategoryDto {

    @Schema(description = "공지사항 카테고리 인덱스", example = "1")
    private Long id;

    @Schema(description = "카테고리명", example = "카테고리명")
    private String title;

    @Schema(description = "메모", example = "메모")
    private String memo;

    @Schema(description = "생성일시", example = "2021-07-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2021-07-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


    private static NoticeCategoryDto.NoticeCategoryDtoBuilder noticeCategoryDtoBuilder(
        NoticeCategory noticeCategory) {
        return NoticeCategoryDto.builder()
            .id(noticeCategory.getId())
            .title(noticeCategory.getTitle())
            .memo(noticeCategory.getMemo())
            .createdAt(noticeCategory.getCreatedAt())
            .updatedAt(noticeCategory.getUpdatedAt());
    }

    public static NoticeCategoryDto convertToDto(NoticeCategory noticeCategory) {
        NoticeCategoryDto noticeCategoryDto = noticeCategoryDtoBuilder(noticeCategory).build();
        return noticeCategoryDto;
    }


}
