package com.example.pinshot.domain.sms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsResponse (
    @Schema(description = "사용자의 전화 번호") String phoneNumber
) {}
