package com.example.pinshot.global.exception.sms;

import com.example.pinshot.global.exception.ErrorCode;
import com.example.pinshot.global.exception.PinShotException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class VerificationCodeExpiredException extends PinShotException {
    public VerificationCodeExpiredException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
