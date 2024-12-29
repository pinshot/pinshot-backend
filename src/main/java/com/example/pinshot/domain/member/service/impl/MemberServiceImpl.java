package com.example.pinshot.domain.member.service.impl;

import com.example.pinshot.domain.member.dto.MemberSignUpRequest;
import com.example.pinshot.domain.member.entity.Member;
import com.example.pinshot.domain.member.repository.MemberRepository;
import com.example.pinshot.domain.member.service.MemberService;
import com.example.pinshot.global.base.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.pinshot.global.base.types.ResponseCode.NOT_FOUND;
import static com.example.pinshot.global.base.types.ResponseCode.SUCCESS;

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
    public ResponseData memberSignUp(MemberSignUpRequest memberSignUpRequest) {
        memberRepository.save(Member.builder()
                .phoneNumber(memberSignUpRequest.phoneNumber())
                .nickname(memberSignUpRequest.nickname())
                .build());

        return ResponseData.of(SUCCESS, "signup success");
    }
}
