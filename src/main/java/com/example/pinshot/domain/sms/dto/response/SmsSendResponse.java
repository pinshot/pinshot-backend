package com.example.pinshot.domain.sms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsSendResponse(
    @Schema(description = "Verifying Token") String verifyingToken
) {
}