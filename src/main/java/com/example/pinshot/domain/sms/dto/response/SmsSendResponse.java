package com.example.pinshot.domain.sms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsSendResponse(
    @Schema(description = "사용자의 전화 번호", example = "01012345678") String phoneNumber,
    @Schema(description = "인증 코드 문자 발송 성공 여부", example = "true") boolean sendSuccess
) {
}