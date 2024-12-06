package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsSendResponse;
import com.example.pinshot.domain.sms.dto.response.SmsVerifyResponse;
import com.example.pinshot.global.exception.sms.VerificationCodeExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService{
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public SmsSendResponse sendSms(SmsSendRequest smsSendRequest){
        // 문자 발송 API 연동 필요!!
        // sendSms 로직 구성
        // 1. random을 통해 인증번호 6자리를 만든다
        // 2. redis에 smsSendRequest를 통해 얻은 전화번호와 random을 통해 만든 인증번호를 set과 ttl을 통해 저장
        // 3. 인증번호를 sms api의 문자 내용에 넣어서 전송 (이 때 문자 발송 api 연동)
        SecureRandom secureRandom = new SecureRandom();
        int verifyCode = secureRandom.nextInt(900000) + 100000; // 6자리 난수의 인증번호 생성

        // redis에 전화번호 : 인증번호를 TTL 3분으로 설정하여 저장
        redisTemplate.opsForValue().set(smsSendRequest.phoneNumber(), String.valueOf(verifyCode), 3, TimeUnit.MINUTES);

        try{
            // 여기에 sms api와 연동 내용 넣기
        }catch (Exception e){

        }
        return new SmsSendResponse(smsSendRequest.phoneNumber());
    }

    @Override
    public SmsVerifyResponse verifySms(SmsVerifyRequest smsVerifyRequest){
        // redis에 저장한 전화번호(String) : 인증번호(String)를 통한 검증 필요
        // verifySms 로직 구성
        // 1. SmsVerifyRequest의 전화번호를 통해 redis에 있는 인증번호를 가져온다
        // 2. 그 인증번호와 SmsVerifyRequest의 인증번호를 비교하여 일치하면 인증 성공 여부를 true로 설정하고, 그 외엔 false로 설정하여 SmsVerifyResponse 반환
        String redisVerifyCode = redisTemplate.opsForValue().get(smsVerifyRequest.phoneNumber());
        if(redisVerifyCode == null) throw new VerificationCodeExpiredException("인증 번호가 만료되었습니다"); // VerificationCodeExpiredException 내용 추가하기

        boolean verifySuccess = redisVerifyCode.equals(smsVerifyRequest.verifyNumber()); // 인증 번호 일치 여부 확인

        return new SmsVerifyResponse(smsVerifyRequest.phoneNumber(), verifySuccess);
    }
}
