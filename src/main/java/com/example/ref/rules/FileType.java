package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum FileType {

    ADMIN_PROFILE_IMAGE("admin_profile_image"),
    USER_PROFILE_IMAGE("user_profile_image"),
    INTRO_IMAGE("intro"),
    INTRO_IMAGE_01("intro_image_01"),
    INTRO_IMAGE_02("intro_image_02"),
    BOARD_NOTICE_IMAGE("notice"),
    BOARD_NOTICE_IMAGE_01("notice_image_01"),
    BOARD_NOTICE_IMAGE_02("notice_image_02"),
    BOARD_NOTICE_IMAGE_03("notice_image_03"),
    BOARD_NOTICE_IMAGE_04("notice_image_04"),
    BOARD_NOTICE_IMAGE_05("notice_image_05");

    private final String path;

    FileType(String path) {
        this.path = path;
    }


}
