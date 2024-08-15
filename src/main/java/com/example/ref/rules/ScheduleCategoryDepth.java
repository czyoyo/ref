package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum ScheduleCategoryDepth {

    SUPER_DEPTH(0);

    private final int depth;

    ScheduleCategoryDepth(int depth) {
        this.depth = depth;
    }

}
