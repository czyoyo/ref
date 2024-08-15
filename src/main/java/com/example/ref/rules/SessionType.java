package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum SessionType {

    SESSION_ID("session-id:");

    private final String type;

    SessionType(String type) {
        this.type = type;
    }
    }
