package com.example.pinshot.domain.sms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsSendResponse(
    @Schema(description = "인증 코드 문자 발송 성공 여부", example = "true") boolean sendSuccess,
    @Schema(description = "Verifying Token") String verifyingToken
) {
}