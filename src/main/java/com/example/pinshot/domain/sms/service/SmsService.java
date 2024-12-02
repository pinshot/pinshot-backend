package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.sms.dto.request.SmsSendRequest;
import com.example.pinshot.domain.sms.dto.request.SmsVerifyRequest;
import com.example.pinshot.domain.sms.dto.response.SmsResponse;

public interface SmsService {
    // 인증 번호 전송 메소드
    SmsResponse sendSms(SmsSendRequest smsSendRequest);
    // 인증 번호 확인 메소드
    SmsResponse verifySms(SmsVerifyRequest smsVerifyRequest);
}
