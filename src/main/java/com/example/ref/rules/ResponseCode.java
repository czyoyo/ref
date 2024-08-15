package com.example.ref.rules;

public enum ResponseCode {

    // 접근 권한
    ACCESS_DENIED("D001", 403, "접근 권한이 없습니다."),
    INVALID_PARAM("P001", 400, "잘못된 요청입니다."),
    INVALID_JWT("J001", 500, "유효하지 않은 토큰입니다."),
    NOT_FOUND("F001", 404, "찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS("A001", 401, "권한이 없습니다."),
    EXPIRED_TOKEN("T001", 401, "토큰이 만료 되었습니다."),
    EXPIRED_REFRESH_TOKEN("T002", 401, "리프레시 토큰이 만료 되었습니다."),
    FAIL("FAIL", 500, "실패"),
    SUCCESS("SUCCESS", 200, "성공");



    private final String code;
    private final int status;
    private final String message;


    ResponseCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }


    public String getCode() { return code; }
    public int getStatus() { return status; }
    public String getMessage() {return message;}

    }
