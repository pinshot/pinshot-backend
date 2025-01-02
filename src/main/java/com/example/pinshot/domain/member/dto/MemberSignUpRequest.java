package com.example.pinshot.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberSignUpRequest(
        @NotBlank(message = "닉네임은 공백이 있을 수 없습니다.")
        @Pattern(regexp = "^[a-z0-9_-]+$", message = "영어 소문자,숫자,특수문자만 사용해주세요.")
        String nickname,

        @NotBlank(message = "전화번호는 공백이 있을 수 없습니다.")
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phoneNumber
) {
}
