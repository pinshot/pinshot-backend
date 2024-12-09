package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsSendResponse;
import com.example.pinshot.domain.sms.dto.response.SmsVerifyResponse;
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
        // 문자 발송 API 연동 필요!!
        // sendSms 로직 구성
        // 1. random을 통해 인증 번호 6자리를 만든다
        // 2. redis에 smsSendRequest를 통해 얻은 전화 번호와 random을 통해 만든 인증 번호를 set과 ttl을 통해 저장
        // 3. 인증번호를 sms api의 문자 내용에 넣어서 전송 (이 때 문자 발송 api 연동)
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
        // redis에 저장한 전화 번호(String) : 인증 번호(String)를 통한 검증 필요
        // verifySms 로직 구성
        // 1. SmsVerifyRequest의 전화 번호를 통해 redis에 있는 인증 번호를 가져온다
        // 2. 그 인증 번호와 SmsVerifyRequest의 인증 번호를 비교하여 일치하면 인증 성공 여부를 true로 설정하고, 그 외엔 false로 설정하여 SmsVerifyResponse 반환
        String redisVerifyCode = redisTemplate.opsForValue().get(smsVerifyRequest.phoneNumber());
        if(redisVerifyCode == null) throw new VerificationCodeExpiredException("인증 번호가 만료되었습니다"); // VerificationCodeExpiredException 내용 추가하기

        boolean verifySuccess = redisVerifyCode.equals(smsVerifyRequest.verifyNumber()); // 인증 번호 일치 여부 확인

        return new SmsVerifyResponse(smsVerifyRequest.phoneNumber(), verifySuccess);
    }
}
