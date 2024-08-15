package com.example.ref.dto;


import com.example.ref.entity.UserCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryDto {

    @Schema(description = "카테고리 인덱스", example = "1")
    private Long id;

    @Schema(description = "권한명", example = "권한명")
    private String title;

    @Schema(description = "카테고리 설명", example = "카테고리 설명")
    private String memo;

    @Schema(description = "카테고리 부모 인덱스", example = "1")
    private UserCategoryDto parent;

    @Schema(description = "카테고리 자식 리스트")
    @Builder.Default
    private List<UserCategoryDto> children = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "카테고리 생성일", example = "2021-01-01 00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "카테고리 뎁스", example = "1")
    private Integer depth;

    @Schema(description = "카테고리 분류", example = "카테고리 분류")
    private String classification;

    private static UserCategoryDto.UserCategoryDtoBuilder userCategoryDtoBuilder(
        UserCategory userCategory){
        return UserCategoryDto.builder()
            .id(userCategory.getId())
            .title(userCategory.getTitle())
            .memo(userCategory.getMemo())
            .depth(userCategory.getDepth())
            .classification(userCategory.getClassification())
            .createdAt(userCategory.getCreatedAt());
    }

    private static UserCategoryDto parentUserCategory(UserCategory userCategory) {
        if(userCategory.getParent() == null) {
            return null;
        }
        return UserCategoryDto.builder()
            .id(userCategory.getParent().getId())
            .title(userCategory.getParent().getTitle())
            .memo(userCategory.getParent().getMemo())
            .depth(userCategory.getParent().getDepth())
            .classification(userCategory.getParent().getClassification())
            .createdAt(userCategory.getParent().getCreatedAt())
            .build();
    }

    private static List<UserCategoryDto> childrenUserCategory(UserCategory userCategory) {
        if(userCategory.getChildren().isEmpty()) {
            return new ArrayList<>();
        }
        return userCategory.getChildren().stream()
            .map(UserCategoryDto::convertToDto)
            .toList();
    }

    public static UserCategoryDto convertToDto(UserCategory userCategory) {
        UserCategoryDtoBuilder userCategoryDtoBuilder = userCategoryDtoBuilder(userCategory);
        userCategoryDtoBuilder.parent(parentUserCategory(userCategory));
        userCategoryDtoBuilder.children(childrenUserCategory(userCategory));
        return userCategoryDtoBuilder.build();
    }
}
