package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum RedisType {

    REFRESH_TOKEN("refresh_token:"), // 리프레시 토큰
    SEARCH("search:"), // 검색
    SEARCH_HISTORY("search-history:"); // 검색 히스토리

    private final String type;

    RedisType(String type) {this.type = type;}

}
