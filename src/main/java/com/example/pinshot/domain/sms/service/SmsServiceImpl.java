package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.member.service.MemberService;
import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsSendResponse;
import com.example.pinshot.domain.sms.dto.response.SmsVerifyResponse;
import com.example.pinshot.global.base.ResponseData;
import com.example.pinshot.global.exception.ErrorCode;
import com.example.pinshot.global.exception.sms.VerificationCodeExpiredException;
import com.example.pinshot.global.jwt.JwtUtil;
import com.example.pinshot.global.jwt.common.UserVo;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

import static com.example.pinshot.global.base.types.ResponseCode.*;
import static com.example.pinshot.global.jwt.TokenType.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SmsServiceImpl implements SmsService{
    @Value("${coolsms.sms-sender}")
    private String smsSender;

    private final DefaultMessageService messageService;
    private final MemberService memberService;

    public SmsServiceImpl(@Value("${coolsms.api-key}") String smsApiKey,
                          @Value("${coolsms.secret-key}") String smsSecretKey,
                          @Value("${coolsms.sms-provider}") String smsProvider,
                          MemberService memberService){
        this.messageService = NurigoApp.INSTANCE.initialize(smsApiKey, smsSecretKey, smsProvider);
        this.memberService = memberService;
    }


    // 인증 번호 요청 (sms 요청)
    @Override
    public ResponseData<SmsSendResponse> sendSms(SmsSendRequest smsSendRequest){
        SecureRandom secureRandom = new SecureRandom();
        String verifyCode = String.valueOf(secureRandom.nextInt(900000) + 100000); // 6자리 난수의 인증 번호 생성
        String verifyingToken = JwtUtil.generateVerifyingToken(smsSendRequest.phoneNumber(), verifyCode);

        Message message = new Message();
        message.setFrom(smsSender); // SMS 문자(인증 번호) 보낼 전화 번호
        message.setTo(smsSendRequest.phoneNumber()); // SMS 문자(인증 번호) 받는 전화 번호
        message.setText("[PinShot] 인증 번호는 " + verifyCode + " 입니다."); // SMS 문자 내용
        SingleMessageSentResponse messageSentResponse = messageService.sendOne(new SingleMessageSendingRequest(message));

        if (messageSentResponse != null
                && (messageSentResponse.getStatusCode().equals("2000") || messageSentResponse.getStatusCode().equals("4000"))) { // 2000 : 정상 접수, 4000 : 수신자가 메세지를 수신함
            return ResponseData.of(SUCCESS, "문자 발송에 성공하였습니다." , new SmsSendResponse(true, verifyingToken)); // 문자 발송에 성공했을 경우
        }
        return ResponseData.of(ERROR, "문자 발송에 실패하였습니다.", new SmsSendResponse(false,"")); // 그 외 나머지 문자 발송 실패
    }

    // 인증 번호 확인
    @Override
    public ResponseData<SmsVerifyResponse> verifySms(String verifyingToken, SmsVerifyRequest smsVerifyRequest){
        if(JwtUtil.checkExpired(verifyingToken)) throw new VerificationCodeExpiredException(ErrorCode.EXPIRED_VERIFICATION_CODE); // verifyingToken이 만료됐는지 확인
        boolean verifySuccess = JwtUtil.getVerifyCode(verifyingToken).verifyCode().equals(smsVerifyRequest.verifyNumber()); // 인증 번호 일치 여부 확인

        UserVo userVo = JwtUtil.getUserVo(verifyingToken);
        boolean isExistMember = memberService.isExistMember(userVo.phoneNumber());

        if(verifySuccess) {
            if (isExistMember) {
                return ResponseData.of(SUCCESS, "로그인에 성공하였습니다.", SmsVerifyResponse.builder()
                        .phoneNumber(userVo.phoneNumber())
                        .accessToken(JwtUtil.generateJwtToken(userVo.phoneNumber(), ACCESS))
                        .refreshToken(JwtUtil.generateJwtToken(userVo.phoneNumber(), REFRESH))
                        .signUpToken(null)
                        .build());
            }

            return ResponseData.of(NOT_FOUND, "회원가입이 필요합니다.", SmsVerifyResponse.builder()
                    .phoneNumber(JwtUtil.getUserVo(verifyingToken).phoneNumber())
                    .accessToken(null)
                    .refreshToken(null)
                    .signUpToken(JwtUtil.generateJwtToken(userVo.phoneNumber(), SIGNUP))
                    .build());
        } else {
            return ResponseData.of(INVALID, "인증 번호가 일치하지 않습니다.");
        }
    }
}
