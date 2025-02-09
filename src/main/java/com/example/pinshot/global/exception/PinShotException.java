package com.example.pinshot.global.exception;

import lombok.Getter;

@Getter
public class PinShotException extends RuntimeException{
    private final ErrorCode errorCode;

    public PinShotException(final ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
