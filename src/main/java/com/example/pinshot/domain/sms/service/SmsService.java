package com.example.pinshot.domain.sms.service;

import com.example.pinshot.domain.sms.dto.request.SmsReqSendDto;
import com.example.pinshot.domain.sms.dto.request.SmsReqVerifyDto;
import com.example.pinshot.domain.sms.dto.response.SmsResDto;

public interface SmsService {
    // 인증 번호 전송 메소드
    SmsResDto sendSms(SmsReqSendDto smsReqSendDTO);
    // 인증 번호 확인 메소드
    SmsResDto verifySms(SmsReqVerifyDto smsReqVerifyDto);
}
