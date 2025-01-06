package com.example.pinshot.domain.member.controller;

import com.example.pinshot.domain.member.dto.SignInRequest;
import com.example.pinshot.domain.member.dto.SignUpRequest;
import com.example.pinshot.domain.member.service.MemberService;
import com.example.pinshot.global.base.ResponseData;
import com.example.pinshot.global.jwt.JwtUtil;
import com.example.pinshot.global.jwt.common.UserVo;
import com.example.pinshot.global.repository.RefreshTokenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.pinshot.global.base.types.ResponseCode.*;
import static com.example.pinshot.global.jwt.TokenType.*;

@Tag(name = "MEMBER API", description = "사용자 확인 및 가입에 대한 API에 대한 명세")
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenRepository tokenRepository;

    @Operation(summary = "사용자 가입 유무 확인", description = "사용자의 핸드폰 번호를 받고 DB에 저장되었는지 확인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 번호 발송 성공",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/get/{phoneNumber}")
    public ResponseEntity<ResponseData> exist(@PathVariable String phoneNumber) {
        boolean isExistMember = memberService.isExistMember(phoneNumber);

        if (isExistMember) {
            return ResponseEntity.ok(ResponseData.of(SUCCESS, "Member exists"));
        }

        Map<String, String> signUpToken = new HashMap<>();
        signUpToken.put("signUpToken", JwtUtil.generateJwtToken(phoneNumber, SIGNUP));

        return ResponseEntity.ok(ResponseData.of(NOT_FOUND, "Member does not exist", signUpToken));
    }

    @Operation(summary = "사용자 회원가입", description = "사용자 회원가입 정보를 받고 DB에 저장")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseData> signUp(
            @RequestHeader("X-SignUp-Token") String signUpToken,
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(memberService.memberSignUp(signUpRequest, signUpToken));
    }

    @Operation(summary = "사용자 로그인", description = "" +
            "사용자가 회원가입 되어 있는 경우 로그인 시 json data에 AccessToken 및 RefreshToken 발급\n " +
            "회원가입 되어 있지 않는 경우에는 json resultCode:NOT_FOUND 반환")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<ResponseData> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        boolean isExistMember = memberService.isExistMember(signInRequest.phoneNumber());

        if(isExistMember) {
            Map<String, String> tokensMap = memberService.generateTokens(signInRequest);

            return ResponseEntity.ok(ResponseData.of(SUCCESS, "sign in success", tokensMap));
        }

        return ResponseEntity.ok(ResponseData.of(NOT_FOUND, "sign in failed"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseData> refresh(@RequestHeader("Authorization") String refreshToken) {
        UserVo userVo = JwtUtil.getUserVo(refreshToken);
        String storedRefreshToken = memberService.getRefreshToken(userVo.phoneNumber());

        if (JwtUtil.checkExpired(refreshToken)) {
            memberService.removeRefreshToken(userVo.phoneNumber());

            return ResponseEntity.status(401)
                    .body(ResponseData.of(EXPIRED, "RefreshToken expired"));
        }

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(401)
                   .body(ResponseData.of(INVALID, "Invalid RefreshToken"));
        }

        String newAccessToken = JwtUtil.generateJwtToken(userVo.phoneNumber(), REFRESH);
        Map<String, String> accessTokenMap = Map.of("accessToken", newAccessToken);
        return ResponseEntity.ok(ResponseData.of(SUCCESS, "generate success", accessTokenMap));
    }

}
