package com.example.ref.config;

import com.example.ref.response.ErrorResponse;
import com.example.ref.rules.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        ErrorResponse responseDto = ErrorResponse.builder()
            .status(ResponseCode.UNAUTHORIZED_ACCESS.getStatus())
            .response(
                ErrorResponse.Response.builder()
                    .code(ResponseCode.UNAUTHORIZED_ACCESS.getCode())
                    .message(ResponseCode.UNAUTHORIZED_ACCESS.getMessage())
                    .build()
            )
            .build();

        String exception = (String) request.getAttribute("exception");

        if (exception != null) {
            if (exception.equals(ResponseCode.EXPIRED_TOKEN.getCode())) {
                responseDto = ErrorResponse.builder()
                    .status(ResponseCode.EXPIRED_TOKEN.getStatus())
                    .response(
                        ErrorResponse.Response.builder()
                            .code(ResponseCode.EXPIRED_TOKEN.getCode())
                            .message(ResponseCode.EXPIRED_TOKEN.getMessage())
                            .build()
                    )
                    .build();
            }
        }

            response.setCharacterEncoding("UTF-8");
            response.setStatus(ResponseCode.UNAUTHORIZED_ACCESS.getStatus()); // 401 : 인증 실패
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));

}
}
