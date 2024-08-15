package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum RefreshSocketType {

    // 타입
    NOTICE_TYPE("notice"),
    USER_TYPE("user"),

    // 상태변경
    CREATE_ACTION("create"),
    UPDATE_ACTION("update"),
    DELETE_ACTION("delete");

    private final String type;

    RefreshSocketType(String type) {
        this.type = type;
    }

}
