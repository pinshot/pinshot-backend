package com.example.pinshot.domain.member.controller;

import com.example.pinshot.domain.member.dto.MemberSignUpRequest;
import com.example.pinshot.domain.member.service.MemberService;
import com.example.pinshot.global.base.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.pinshot.global.base.types.ResponseCode.NOT_FOUND;
import static com.example.pinshot.global.base.types.ResponseCode.SUCCESS;

@Tag(name = "MEMBER API", description = "사용자 확인 및 가입에 대한 API에 대한 명세")
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

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

        return ResponseEntity.ok(ResponseData.of(NOT_FOUND, "Member does not exist"));
    }

    @Operation(summary = "사용자 회원가입", description = "사용자 회원가입 정보를 받고 DB에 저장")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseData> signup(@Valid @RequestBody MemberSignUpRequest memberSignUpRequest) {
        return ResponseEntity.ok(memberService.memberSignUp(memberSignUpRequest));
    }

}
