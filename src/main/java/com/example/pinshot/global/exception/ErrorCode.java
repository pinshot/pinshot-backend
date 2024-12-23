package com.example.pinshot.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /** 4XX **/
    EXPIRED_VERIFICATION_CODE(400, "SMS-ERR-400", "인증 번호가 만료되었습니다."),
    INVALID_JWT_TOKEN_TYPE(400, "JWT-ERR-400", "JWT 토큰 타입이 잘못되었습니다.");

    private final int status;
    private final String errorCode;
    private final String message;
}
