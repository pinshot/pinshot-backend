package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsSendResponse;
import com.example.pinshot.domain.sms.dto.response.SmsVerifyResponse;
import com.example.pinshot.global.exception.ErrorCode;
import com.example.pinshot.global.exception.sms.VerificationCodeExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SmsServiceImpl implements SmsService{
    @Value("${coolsms.sms-sender}")
    private String smsSender;

    private final DefaultMessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    public SmsServiceImpl(@Value("${coolsms.api-key}") String smsApiKey,
                          @Value("${coolsms.secret-key}") String smsSecretKey,
                          @Value("${coolsms.sms-provider}") String smsProvider,
                          RedisTemplate<String, String> redisTemplate){
        this.messageService = NurigoApp.INSTANCE.initialize(smsApiKey, smsSecretKey, smsProvider);
        this.redisTemplate = redisTemplate;
    }


    @Override
    public SmsSendResponse sendSms(SmsSendRequest smsSendRequest){
        SecureRandom secureRandom = new SecureRandom();
        int verifyCode = secureRandom.nextInt(900000) + 100000; // 6자리 난수의 인증 번호 생성
        // redis에 전화 번호 : 인증 번호를 TTL 3분으로 설정하여 저장
        redisTemplate.opsForValue().set(smsSendRequest.phoneNumber(), String.valueOf(verifyCode), 3, TimeUnit.MINUTES);

        Message message = new Message();
        message.setFrom(smsSender); // SMS 문자(인증 번호) 보낼 전화 번호
        message.setTo(smsSendRequest.phoneNumber()); // SMS 문자(인증 번호) 받는 전화 번호
        message.setText("[PinShot] 인증 번호는 " + verifyCode + " 입니다."); // SMS 문자 내용
        SingleMessageSentResponse messageSentResponse = messageService.sendOne(new SingleMessageSendingRequest(message));

        if (messageSentResponse != null
                && (messageSentResponse.getStatusCode().equals("2000") || messageSentResponse.getStatusCode().equals("4000"))) { // 2000 : 정상 접수, 4000 : 수신자가 메세지를 수신함
            return new SmsSendResponse(smsSendRequest.phoneNumber(), true); // 문자 발송에 성공했을 경우
        }
        return new SmsSendResponse(smsSendRequest.phoneNumber(), false); // 그 외 나머지 문자 발송 실패
    }

    @Override
    public SmsVerifyResponse verifySms(SmsVerifyRequest smsVerifyRequest){
        String redisVerifyCode = redisTemplate.opsForValue().get(smsVerifyRequest.phoneNumber());
        if(redisVerifyCode == null) throw new VerificationCodeExpiredException(ErrorCode.EXPIRED_VERIFICATION_CODE); // VerificationCodeExpiredException 내용 추가하기

        boolean verifySuccess = redisVerifyCode.equals(smsVerifyRequest.verifyNumber()); // 인증 번호 일치 여부 확인

        return new SmsVerifyResponse(smsVerifyRequest.phoneNumber(), verifySuccess);
    }
}
