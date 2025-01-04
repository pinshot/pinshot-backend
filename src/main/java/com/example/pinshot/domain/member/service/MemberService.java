package com.example.pinshot.domain.member.service;

import com.example.pinshot.domain.member.dto.SignInRequest;
import com.example.pinshot.domain.member.dto.SignUpRequest;
import com.example.pinshot.global.base.ResponseData;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface MemberService {
    @Transactional(readOnly = true)
    boolean isExistMember(String phoneNumber);

    @Transactional
    ResponseData memberSignUp(SignUpRequest signUpRequest, String signUpToken);

    Map<String, String> generateTokens(SignInRequest signInRequest);

    String getRefreshToken(String phoneNumber);

    void removeRefreshToken(String phoneNumber);
}
