package com.example.ref.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    @Schema(description = "파일 인덱스", example = "1")
    private Long id;

    @Schema(description = "원래 파일명", example = "원래 파일명")
    private String originFileName;

    @Schema(description = "저장된 파일명", example = "저장된 파일명")
    private String savedFileName;

    @Schema(description = "파일 크기", example = "1000")
    private Long fileSize;

    @Schema(description = "파일 경로", example = "파일 경로")
    private String filePath;

    @Schema(description = "파일 확장자", example = "jpg")
    private String fileExtension;

    @Schema(description = "URL", example = "URL")
    private String url;
}
