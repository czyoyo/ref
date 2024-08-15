package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum AdminType {

    ADMIN("ROLE_ADMIN");

    private final String type;

    AdminType(String type) {this.type = type;}

}
