package com.example.ref.config;

import com.example.ref.response.ErrorResponse;
import com.example.ref.rules.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ErrorResponse responseDto = ErrorResponse.builder()
            .status(ResponseCode.ACCESS_DENIED.getStatus())
            .response(
                ErrorResponse.Response.builder()
                    .code(ResponseCode.ACCESS_DENIED.getCode())
                    .message(ResponseCode.ACCESS_DENIED.getMessage())
                    .build()
            )
            .build();

        response.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);// json 형태로 보내줌
        response.setStatus(ResponseCode.ACCESS_DENIED.getStatus()); // 403 : 접근 권한 없음
        response.getWriter().write(objectMapper.writeValueAsString(responseDto)); // json 형태로 변환하여 보냄
    }
}
