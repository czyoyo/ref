package com.example.ref.dto;

import com.example.ref.entity.AdminProfileImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileImageDto {

    @Schema(description = "어드민 프로필 이미지 인덱스", example = "1")
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

    private static AdminProfileImageDto.AdminProfileImageDtoBuilder userProfileImageDtoBuilder(
        AdminProfileImage adminProfileImage){
        return AdminProfileImageDto.builder()
            .id(adminProfileImage.getId())
            .fileExtension(adminProfileImage.getFileExtension())
            .filePath(adminProfileImage.getFilePath())
            .originFileName(adminProfileImage.getOriginFileName())
            .savedFileName(adminProfileImage.getSavedFileName());
    }

    public static AdminProfileImageDto convertToDto(AdminProfileImage adminProfileImage) {
        AdminProfileImageDtoBuilder adminProfileImageDtoBuilder = userProfileImageDtoBuilder(adminProfileImage);
        return adminProfileImageDtoBuilder.build();
    }


}
