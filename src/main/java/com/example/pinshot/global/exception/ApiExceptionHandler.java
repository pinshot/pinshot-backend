package com.example.pinshot.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(PinShotException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(PinShotException ex){
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(new ApiExceptionResponse(errorCode.getErrorCode(), errorCode.getMessage()));
    }
}