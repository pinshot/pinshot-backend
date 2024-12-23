package com.example.pinshot.domain.sms.controller;

import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsSendResponse;
import com.example.pinshot.domain.sms.dto.response.SmsVerifyResponse;
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
    private final SmsService smsService;

    // Swagger 어노테이션에 대하여)

    // @Operation : API(컨트롤러 메소드) 설명.
    // -> summary == 간단한 요약(API 제목), description == 세부 설명(API 설명)

    // @ApiResponses : Swagger UI에서 각 API의 response를 보여주려면 필수적으로 사용해야 함!
    // -> @io.swagger.v3.oas.annotations.responses.ApiResponse == @ApiResponse's'는 응답 여러 개를 묶은 것인데, 그 중에 한 response에 대하여 작성
    // -> responseCode == 응답 코드(ex. 200, 404 등등), description == response의 세부 설명
    // -> content와 그 뒤의 내용 == 실제 response로 지정할 클래스

    // @Parameter : API의 Reqeust(DTO)에 대한 설명
    // description == request의 세부 설명

    @PostMapping("/send")
    @Operation(summary = "인증 번호 요청", description = "전화번호 인증을 위한 인증 번호를 요청합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 번호 발송 성공",
                    content = @Content(schema = @Schema(implementation = SmsSendResponse.class)))
    })
    public ResponseEntity<?> sendSmsCode(
            @Parameter(description = "인증 번호를 보낼 사용자의 전화 번호를 담고 있는 DTO")
            @RequestBody SmsSendRequest smsSendRequest
    ){
        return ResponseEntity.ok(smsService.sendSms(smsSendRequest));
    }

    @PostMapping("/verify")
    @Operation(summary = "인증 번호 확인", description = "사용자가 입력한 인증 번호를 확인합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전화 번호 인증 성공",
                    content = @Content(schema = @Schema(implementation = SmsVerifyResponse.class)))
    })
    public ResponseEntity<?> verifySmsCode(
            @RequestHeader("Verifying") String verifyingToken,
            @Parameter(description = "사용자가 입력한 인증 번호를 담고 있는 DTO")
            @RequestBody SmsVerifyRequest smsVerifyRequest
    ){
        return ResponseEntity.ok(smsService.verifySms(verifyingToken, smsVerifyRequest));
    }
}
