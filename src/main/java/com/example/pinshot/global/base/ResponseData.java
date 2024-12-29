package com.example.pinshot.global.base;

import com.example.pinshot.global.base.types.ResponseCode;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseData<T> {
    private ResponseCode resultCode;
    private String message;
    private T data;

    public static <T> ResponseData<T> of(ResponseCode resultCode, String message, T data) {
        return new ResponseData<>(resultCode, message, data);
    }

    public static <T> ResponseData<T> of(ResponseCode resultCode, String message) {
        return new ResponseData<>(resultCode, message, null);
    }


}
