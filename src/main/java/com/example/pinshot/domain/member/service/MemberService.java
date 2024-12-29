package com.example.pinshot.domain.member.service;

import com.example.pinshot.domain.member.dto.MemberSignUpRequest;
import com.example.pinshot.global.base.ResponseData;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {
    @Transactional(readOnly = true)
    boolean isExistMember(String phoneNumber);

    ResponseData memberSignUp(MemberSignUpRequest memberSignUpRequest);
}
