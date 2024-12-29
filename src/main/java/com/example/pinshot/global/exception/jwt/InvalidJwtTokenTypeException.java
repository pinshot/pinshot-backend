package com.example.pinshot.global.exception.jwt;

import com.example.pinshot.global.exception.ErrorCode;
import com.example.pinshot.global.exception.PinShotException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class InvalidJwtTokenTypeException extends PinShotException {
  public InvalidJwtTokenTypeException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
