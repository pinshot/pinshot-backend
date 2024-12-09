package com.example.pinshot.domain.sms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsVerifyResponse(
        @Schema(description = "사용자의 전화번호") String phoneNumber,
        @Schema(description = "사용자 인증 성공 여부") boolean verifySuccess
) {
}