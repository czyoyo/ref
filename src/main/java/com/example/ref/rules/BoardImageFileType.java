package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum BoardImageFileType {

    BOARD_NOTICE_IMAGE_01("notice_01"),
    BOARD_NOTICE_IMAGE_02("notice_02"),
    BOARD_NOTICE_IMAGE_03("notice_03"),
    BOARD_NOTICE_IMAGE_04("notice_04"),
    BOARD_NOTICE_IMAGE_05("notice_05");

    private final String path;

    BoardImageFileType(String path) {
        this.path = path;
    }
}
