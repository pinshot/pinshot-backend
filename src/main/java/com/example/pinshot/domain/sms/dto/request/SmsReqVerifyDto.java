package com.example.pinshot.domain.sms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsReqVerifyDto {
    @Schema(description = "사용자의 전화 번호")
    private String phoneNumber;

    @Schema(description = "사용자가 입력한 인증 번호")
    private String verifyNumber;
}
