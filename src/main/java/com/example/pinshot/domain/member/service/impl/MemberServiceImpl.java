package com.example.pinshot.domain.member.service.impl;

import com.example.pinshot.domain.member.dto.MemberSignUpRequest;
import com.example.pinshot.domain.member.entity.Member;
import com.example.pinshot.domain.member.repository.MemberRepository;
import com.example.pinshot.domain.member.service.MemberService;
import com.example.pinshot.global.base.ResponseData;
import com.example.pinshot.global.exception.jwt.InvalidJwtTokenTypeException;
import com.example.pinshot.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.pinshot.global.base.types.ResponseCode.SUCCESS;
import static com.example.pinshot.global.exception.ErrorCode.EXPIRED_SIGNUP_CODE;
import static com.example.pinshot.global.jwt.TokenType.SIGNUP;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    @Override
    public boolean isExistMember(String phoneNumber){
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);

        return member.isPresent();
    }

    @Override
    public ResponseData memberSignUp(MemberSignUpRequest memberSignUpRequest, String signUpToken) {
        if(JwtUtil.checkExpired(signUpToken)) throw new InvalidJwtTokenTypeException(EXPIRED_SIGNUP_CODE);

        memberRepository.save(Member.builder()
                .phoneNumber(memberSignUpRequest.phoneNumber())
                .nickname(memberSignUpRequest.nickname().replaceAll("\\s", ""))
                .build());

        return ResponseData.of(SUCCESS, "signup success");
    }

    @Override
    public String generateSignUpToken(String phoneNumber) {
        return JwtUtil.generateJwtToken(phoneNumber, SIGNUP);
    }
}
