package com.example.pinshot.domain.sms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmsSendRequest(
        // @Schema는 DTO 필드에 관한 간단한 설명
        @Schema(description = "사용자의 전화 번호") String phoneNumber
) {
}