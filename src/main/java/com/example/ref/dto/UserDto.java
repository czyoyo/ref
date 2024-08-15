package com.example.ref.dto;

import com.example.ref.entity.Admin;
import com.example.ref.entity.AdminProfileImage;
import com.example.ref.entity.User;
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
public class UserDto {

    @Schema(description = "유저 인덱스")
    private Long id;

    @Schema(description = "유저 아이디")
    private String userId;

    @Schema(description = "유저 비밀번호")
    private String password;

    @Schema(description = "유저 닉네임")
    private String nickname;

    @Schema(description = "유저 메모")
    private String memo;

    @Schema(description = "유저 승인 레벨")
    private Integer approvedLevel;

    @Schema(description = "외부 검사 여부")
    private String typeInspection;

    @Schema(description = "담당 어드민 정보")
    private AdminDto admin;

    @Schema(description = "유저 권한 카테고리 정보")
    private UserCategoryDto userCategory;

    @Schema(description = "관리자가 변경한 레벨")
    private Integer adminChangeLevel;

    @Schema(description = "완료 일정 수")
    private Integer completeScheduleCount;

    @Schema(description = "생성일시", example = "2021-07-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "프로필 이미지")
    private UserProfileImageDto profileImage;


    private static UserDto.UserDtoBuilder userDtoBuilder(User user){
        return UserDto.builder()
            .id(user.getId())
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .memo(user.getMemo())
            .approvedLevel(user.getApprovedLevel())
            .typeInspection(user.getTypeInspection())
            .adminChangeLevel(user.getAdminChangeLevel())
            .createdAt(user.getCreatedAt())
            ;
    }

    private static UserProfileImageDto userProfileImage(User user) {
        if(user.getUserProfileImage() == null) {
            return null;
        }
        return UserProfileImageDto.builder()
            .id(user.getUserProfileImage().getId())
            .fileExtension(user.getUserProfileImage().getFileExtension())
            .filePath(user.getUserProfileImage().getFilePath())
            .originFileName(user.getUserProfileImage().getOriginFileName())
            .savedFileName(user.getUserProfileImage().getSavedFileName())
            .url(user.getUserProfileImage().getUrl())
            .build();
    }

    private static AdminProfileImageDto createAdminProfileImageDto(
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

    private static AdminDto admin(User user) {
        if(user.getAdmin() == null) {
            return null;
        }
        return AdminDto.builder()
            .id(user.getAdmin().getId())
            .nickname(user.getAdmin().getNickname())
            .profileImage(createAdminProfileImageDto(user.getAdmin()))
            .build();
    }


    private static UserCategoryDto userCategory(User user) {
        if(user.getUserCategory() == null) {
            return null;
        }
        return UserCategoryDto.builder()
            .id(user.getUserCategory().getId())
            .title(user.getUserCategory().getTitle())
            .build();
    }

    public static UserDto convertToDto(User user, boolean isProfileImage) {
        UserDtoBuilder userDtoBuilder = userDtoBuilder(user);
        userDtoBuilder.userCategory(userCategory(user));
        userDtoBuilder.admin(admin(user));
        if (isProfileImage) {
            userDtoBuilder.profileImage(userProfileImage(user));
        }
        return userDtoBuilder.build();
    }

}
