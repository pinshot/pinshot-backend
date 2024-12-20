package com.example.pinshot.domain.sms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsVerifyRequest(
        @Schema(description = "사용자가 입력한 인증 번호") String verifyNumber
) {
}