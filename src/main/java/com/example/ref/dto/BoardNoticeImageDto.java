package com.example.ref.dto;

import com.example.ref.entity.BoardNoticeImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardNoticeImageDto {

    @Schema(description = "이미지 인덱스", example = "1")
    private Long id;

    @Schema(description = "게시글 인덱스", example = "1")
    private Long boardNoticeId;

    @Schema(description = "원본 파일명", example = "원본 파일명")
    private String originFileName;

    @Schema(description = "저장된 파일명", example = "저장된 파일명")
    private String savedFileName;

    @Schema(description = "파일 확장자", example = "jpg")
    private String fileExtension;

    @Schema(description = "파일 경로", example = "파일 경로")
    private String filePath;

    @Schema(description = "URL", example = "URL")
    private String url;


    private static BoardNoticeImageDto.BoardNoticeImageDtoBuilder boardNoticeImageDtoBuilder(
        BoardNoticeImage boardNoticeImage) {
        return BoardNoticeImageDto.builder()
            .id(boardNoticeImage.getId())
            .originFileName(boardNoticeImage.getOriginFileName())
            .savedFileName(boardNoticeImage.getSavedFileName())
            .fileExtension(boardNoticeImage.getFileExtension())
            .filePath(boardNoticeImage.getFilePath())
            .url(boardNoticeImage.getUrl());
    }


    public static BoardNoticeImageDto convertToDto(BoardNoticeImage boardNoticeImage) {
        BoardNoticeImageDtoBuilder builder = boardNoticeImageDtoBuilder(boardNoticeImage);
        return builder.build();
    }

}
