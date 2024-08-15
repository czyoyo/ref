package com.example.ref.config;

import com.example.ref.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 모든 에러 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR  , ex.getMessage()); // 클라이언트에 노출되지 않도록 기본 메시지 사용
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 400 에러
    @ExceptionHandler({
        IllegalArgumentException.class, // 잘못된 인자
    })
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // message 가 있는지 default message 인지 확인
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        message = switch (message) {
            case "비어 있을 수 없습니다" -> "필수 입력값이 비어있습니다.";
            default -> message;
        };
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
        ExpiredJwtException.class // JWT 토큰 만료
    })
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
        Unauthorized.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Unauthorized ex) {
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({
        ConstraintViolationException.class // JPA DB 제약조건 위반
    })
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = extractValidationErrorMessage(ex);
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractValidationErrorMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .findFirst()
            .orElse("Validation failed");
    }

    private ErrorResponse getErrorResponse(HttpStatus httpStatus, String message) {
        return ErrorResponse.builder()
            .status(httpStatus.value())
            .response(
                ErrorResponse.Response.builder()
                    .code(httpStatus.name())
                    .message(message)
                    .build()
            )
            .build();
    }

}
