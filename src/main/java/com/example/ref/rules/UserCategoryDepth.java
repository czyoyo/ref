package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum UserCategoryDepth {
    SUPER_DEPTH(0),
    MIDDLE_DEPTH(1),
    LOW_DEPTH(2);

    private final int depth;

    UserCategoryDepth(int depth) {this.depth = depth;}

}
