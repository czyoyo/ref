package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum GuestType {

    GUEST("ROLE_GUEST"),
    ADMIN_GUEST("ROLE_ADMIN_GUEST"),
    USER_GUEST("ROLE_USER_GUEST"),
    ;

    private final String type;

    GuestType(String type) {
        this.type = type;
    }
}
