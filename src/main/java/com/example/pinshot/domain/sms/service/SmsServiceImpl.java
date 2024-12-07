package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService{

    @Override
    public SmsResponse sendSms(SmsSendRequest smsSendRequest){
        // 문자 발송 API 연동 필요
        return new SmsResponse(null);
    }

    @Override
    public SmsResponse verifySms(SmsVerifyRequest smsVerifyRequest){
        // redis에 저장한 전화번호 : 인증번호 Hash를 통한 검증 필요
        return new SmsResponse(null);
    }
}
