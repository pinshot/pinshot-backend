package com.example.pinshot.global.exception;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ApiExceptionResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public ApiExceptionResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
