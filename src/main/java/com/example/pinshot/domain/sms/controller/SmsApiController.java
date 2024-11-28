package com.example.pinshot.domain.sms.controller;

import com.example.pinshot.domain.sms.dto.request.SmsReqSendDto;
import com.example.pinshot.domain.sms.dto.request.SmsReqVerifyDto;
import com.example.pinshot.domain.sms.dto.response.SmsResDto;
import com.example.pinshot.domain.sms.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sms")
@Tag(name = "SMS API", description = "전화 번호 인증을 위한 SMS API에 대한 명세")
public class SmsApiController {
    // 필요한 api
    // 1. 인증번호 요청 api -> 이후 문자 발송 API와의 연동을 통한 로직 구현 필요
    // => request : 전화 번호
    // => response : 상태 코드, 전화 번호
    // 2. 인증번호 확인 api
    // => request : 전화 번호, 인증 번호
    // => response : 상태 코드, 전화 번호

    private final SmsService smsService;

    @PostMapping("/send")
    @Operation(summary = "인증 번호 요청", description = "전화번호 인증을 위한 인증 번호를 요청합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 번호 발송 성공",
                    content = @Content(schema = @Schema(implementation = SmsResDto.class)))
    })
    public ResponseEntity<?> sendSmsCode(
            @Parameter(description = "인증 번호를 보낼 사용자의 전화 번호를 담고 있는 DTO")
            @RequestBody SmsReqSendDto smsReqSendDTO
    ){
        return ResponseEntity.ok(smsService.sendSms(smsReqSendDTO));
    }

    @PostMapping("/verify")
    @Operation(summary = "인증 번호 확인", description = "사용자가 입력한 인증 번호를 확인합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전화 번호 인증 성공",
                    content = @Content(schema = @Schema(implementation = SmsResDto.class)))
    })
    public ResponseEntity<?> verifySmsCode(
            @Parameter(description = "사용자가 입력한 인증 번호를 담고 있는 DTO")
            @RequestBody SmsReqVerifyDto smsReqVerifyDto
    ){
        return ResponseEntity.ok(smsService.verifySms(smsReqVerifyDto));
    }
}
