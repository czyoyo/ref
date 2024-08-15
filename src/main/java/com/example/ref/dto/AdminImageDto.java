package com.example.ref.dto;

import com.example.ref.entity.AdminImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
public class AdminImageDto {

    @Schema(description = "어드민 이미지 인덱스", example = "1")
    private Long id;

    @Schema(description = "파일 확장자", example = "png")
    private String fileExtension;

    @Schema(description = "파일 경로", example = "project/backend/upload/profile/20240201/1706766402685_68131665829c44c6a32cd2ec5547122c.png")
    private String filePath;

    @Schema(description = "원본 파일명", example = "테스트1.png")
    private String originFileName;

    @Schema(description = "저장된 파일명", example = "1706766402685_68131665829c44c6a32cd2ec5547122c.png")
    private String savedFileName;

    @Schema(description = "이미지 URL", example = "https://example.kr.object.gov-ncloudstorage.com/project/backend/upload/profile/20240201/1706766402685_68131665829c44c6a32cd2ec5547122c.png")
    private String url;

    @Schema(description = "이미지 타입", example = "ex) intro_01")
    private String type;

    private static AdminImageDto.AdminImageDtoBuilder adminImageDtoBuilder(
        AdminImage adminImage){
        return AdminImageDto.builder()
            .id(adminImage.getId())
            .fileExtension(adminImage.getFileExtension())
            .filePath(adminImage.getFilePath())
            .originFileName(adminImage.getOriginFileName())
            .savedFileName(adminImage.getSavedFileName());
    }

    public static AdminImageDto convertToDto(AdminImage adminImage) {
        AdminImageDtoBuilder adminImageDtoBuilder = adminImageDtoBuilder(adminImage);
        return adminImageDtoBuilder.build();
    }

}
