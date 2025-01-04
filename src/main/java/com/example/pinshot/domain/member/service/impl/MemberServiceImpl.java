package com.example.pinshot.domain.member.service.impl;

import com.example.pinshot.domain.member.dto.SignInRequest;
import com.example.pinshot.domain.member.dto.SignUpRequest;
import com.example.pinshot.domain.member.entity.Member;
import com.example.pinshot.domain.member.repository.MemberRepository;
import com.example.pinshot.domain.member.service.MemberService;
import com.example.pinshot.global.base.ResponseData;
import com.example.pinshot.global.exception.jwt.InvalidJwtTokenTypeException;
import com.example.pinshot.global.jwt.JwtUtil;
import com.example.pinshot.global.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static com.example.pinshot.global.base.types.ResponseCode.SUCCESS;
import static com.example.pinshot.global.exception.ErrorCode.EXPIRED_SIGNUP_CODE;
import static com.example.pinshot.global.jwt.TokenType.ACCESS;
import static com.example.pinshot.global.jwt.TokenType.REFRESH;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository tokenRepository;

    @Override
    public boolean isExistMember(String phoneNumber){
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);

        return member.isPresent();
    }

    @Override
    public ResponseData memberSignUp(SignUpRequest signUpRequest, String signUpToken) {
        if(JwtUtil.checkExpired(signUpToken)) throw new InvalidJwtTokenTypeException(EXPIRED_SIGNUP_CODE);

        memberRepository.save(Member.builder()
                .phoneNumber(signUpRequest.phoneNumber())
                .nickname(signUpRequest.nickname().replaceAll("\\s", ""))
                .build());

        return ResponseData.of(SUCCESS, "signup success");
    }

    @Override
    public Map<String, String> generateTokens(SignInRequest signInRequest) {
        String accessToken = JwtUtil.generateJwtToken(signInRequest.phoneNumber(), ACCESS);
        String refreshToken = JwtUtil.generateJwtToken(signInRequest.phoneNumber(), REFRESH);

        tokenRepository.saveRefreshToken(signInRequest.phoneNumber(), refreshToken);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    @Override
    public String getRefreshToken(String phoneNumber) {
        return tokenRepository.findPhoneNumberByRefreshToken(phoneNumber);
    }

    @Override
    public void removeRefreshToken(String phoneNumber) {
        tokenRepository.deleteRefreshToken(phoneNumber);
    }
}
