package com.example.pinshot.domain.sms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record SmsVerifyResponse(
        @Schema(description = "사용자의 전화번호") String phoneNumber,
        @Schema(description = "Access Token") String accessToken,
        @Schema(description = "Refresh Token") String refreshToken,
        @Schema(description = "SignUp Token") String signUpToken
) {
    @Builder
    public SmsVerifyResponse {} // 로그인 또는 회원가입 필요 경우를 나누기 위해 Builder를 사용해서 경우에 따라 필요한 토큰을 넣음
}