package com.example.ref.dto;

import com.example.ref.entity.Admin;
import com.example.ref.entity.AdminImage;
import com.example.ref.entity.AdminProfileImage;
import com.example.ref.rules.FileType;
import com.example.ref.util.AuthUtils;
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
public class AdminDto {

    @Schema(description = "어드민 인덱스")
    private Long id;

    @Schema(description = "어드민 아이디")
    private String adminId;

    @Schema(description = "어드민 닉네임")
    private String nickname;

    @Schema(description = "어드민 소개")
    private String adminAbout;

    @Schema(description = "어드민 메모")
    private String memo;

    @Schema(description = "어드민 프로필 이미지")
    private AdminProfileImageDto profileImage;

    @Schema(description = "어드민 인트로 이미지 01")
    private AdminImageDto introImage01;

    @Schema(description = "어드민 인트로 이미지 02")
    private AdminImageDto introImage02;

    @Schema(description = "어드민 카테고리 리스트")
    private UserCategoryDto userCategory;

    @Schema(description = "권한")
    private String auth;

    @Schema(description = "부모 권한")
    private UserCategoryDto parentAuth;

    @Schema(description = "생성일시", example = "2021-07-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    private static AdminDto.AdminDtoBuilder adminDtoBuilder(Admin admin){
        return AdminDto.builder()
            .id(admin.getId())
            .adminId(admin.getAdminId())
            .nickname(admin.getNickname())
            .adminAbout(admin.getAdminAbout())
            .memo(admin.getMemo())
            .auth(AuthUtils.getRole(admin))
            .createdAt(admin.getCreatedAt())
            ;
    }


    public static AdminDto convertToDto(Admin admin, boolean isProfileImage, boolean isAdminAboutImageList) {

        AdminDtoBuilder builder = adminDtoBuilder(admin);

        if(isProfileImage) {
            builder.profileImage(
                createProfileImageDto(admin)
            );
        }

        if(admin.getUserCategory() != null && admin.getUserCategory().getParent() != null) {
            builder.parentAuth(parentUserCategory(admin));
        }

        if(isAdminAboutImageList) {

            builder.introImage01(
                createIntroImageDto(admin, FileType.INTRO_IMAGE_01)
            );

            builder.introImage02(
                createIntroImageDto(admin, FileType.INTRO_IMAGE_02)
            );

        }

        builder.userCategory(userCategory(admin));

        return builder.build();
    }

    private static UserCategoryDto parentUserCategory(Admin admin) {
        if(admin.getUserCategory() == null) {
            return null;
        }
        if(admin.getUserCategory().getParent() == null) {
            return null;
        }
        return UserCategoryDto.builder()
            .id(admin.getUserCategory().getParent().getId())
            .title(admin.getUserCategory().getParent().getTitle())
            .build();
    }


    private static UserCategoryDto userCategory(Admin admin) {
        if(admin.getUserCategory() == null) {
            return null;
        }
        return UserCategoryDto.builder()
            .id(admin.getUserCategory().getId())
            .title(admin.getUserCategory().getTitle())
            .build();
    }

    private static AdminImageDto createIntroImageDto(Admin admin, FileType fileType) {

        AdminImage introImage = admin.getAdminImageList().stream()
            .filter(image -> image.getType().equals(fileType.getPath()))
            .findFirst()
            .orElse(null);

        if(introImage == null){
            return null;
        }

        return AdminImageDto.builder()
            .id(introImage.getId())
            .originFileName(introImage.getOriginFileName())
            .savedFileName(introImage.getSavedFileName())
            .fileExtension(introImage.getFileExtension())
            .filePath(introImage.getFilePath())
            .url(introImage.getUrl())
            .type(introImage.getType())
            .build();

    }


    private static AdminProfileImageDto createProfileImageDto(
        Admin admin) {

        if(admin.getAdminProfileImage() == null){
            return null;
        }

        AdminProfileImage profileImage = admin.getAdminProfileImage();

        return AdminProfileImageDto.builder()
            .id(profileImage.getId())
            .originFileName(profileImage.getOriginFileName())
            .savedFileName(profileImage.getSavedFileName())
            .fileExtension(profileImage.getFileExtension())
            .filePath(profileImage.getFilePath())
            .url(profileImage.getUrl())
            .build();
    }

}
