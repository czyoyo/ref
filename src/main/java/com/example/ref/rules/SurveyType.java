package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum SurveyType {

    CHECK("check"),
    RADIO("radio"),
    TEXT("text"),
    PROGRESS("progress"),
    END("end"),
    ALL("all"),
    WAIT("wait")
    ;

    private final String type;

    SurveyType(String type) {
        this.type = type;
    }
}
