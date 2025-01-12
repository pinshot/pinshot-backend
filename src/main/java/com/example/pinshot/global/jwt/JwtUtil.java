package com.example.pinshot.global.jwt;

import com.example.pinshot.global.jwt.common.SmsVo;
import com.example.pinshot.global.jwt.common.UserVo;
import com.example.pinshot.global.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {
    private static SecretKey JWT_SECRET_KEY;
    private static final long VERIFYING_TOKEN_EXPIRATION_TIME = 1000L * 60 * 3; // VerifyingToken 만료 시간, 3분
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60; // AccessToken 만료 시간, 1시간
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // RefreshToken 만료 시간, 1주일
    private static final long SIGNUP_TOKEN_EXPIRATION_TIME = 1000L * 60 * 5; // SignUpToken 만료 시간, 5분

    // jwt 시크릿 키 주입
    public JwtUtil(@Value("${jwt.secret}") String jwtSecretKey, RefreshTokenRepository tokenRepository) {
        JWT_SECRET_KEY = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // jwt 토큰용 Claims 생성 (VerifyingToken 제외)
    private static Map<String, Object> createClaims(String phoneNumber){

        return Jwts.claims().setSubject(Aes256Util.encrypt(phoneNumber));
    }

    // VerifyingToken 토큰용 Claims 생성
    private static Map<String, Object> createVerifyingClaims(String phoneNumber, String verifyCode){
        Map<String, Object> verifyingClaims = new HashMap<>();
        verifyingClaims.put("phoneNumber", Aes256Util.encrypt(phoneNumber));
        verifyingClaims.put("verifyCode", Aes256Util.encrypt(verifyCode));
        return verifyingClaims;
    }

    // 토큰 타입에 따른 각각의 jwt 토큰 생성 (VerifyingToken 제외)
    public static String generateJwtToken(String phoneNumber, TokenType tokenType) {
        long now = System.currentTimeMillis(); // JWT 토큰 생성 시간
        long expirationTime = switch (tokenType) {
            case ACCESS -> ACCESS_TOKEN_EXPIRATION_TIME;
            case REFRESH ->  REFRESH_TOKEN_EXPIRATION_TIME;
            case SIGNUP -> SIGNUP_TOKEN_EXPIRATION_TIME;
        };

        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setClaims(createClaims(phoneNumber))
                .setExpiration(new Date(now + expirationTime))
                .signWith(JWT_SECRET_KEY)
                .compact();
    }

    // VerifyingToken 토큰 생성
    public static String generateVerifyingToken(String phoneNumber, String verifyCode) {
        long now = System.currentTimeMillis(); // JWT 토큰 생성 시간

        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setClaims(createVerifyingClaims(phoneNumber, verifyCode))
                .setExpiration(new Date(now + VERIFYING_TOKEN_EXPIRATION_TIME))
                .signWith(JWT_SECRET_KEY)
                .compact();
    }

    // jwt 토큰에서 사용자 정보 추출
    public static UserVo getUserVo(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new UserVo(Aes256Util.decrypt(claims.getSubject()));
    }

    // jwt 토큰에서 인증 번호 추출 (VerifyingToken 토큰)
    public static SmsVo getVerifyCode(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //
        return new SmsVo(Aes256Util.decrypt(claims.get("verifyCode", String.class)));
    }

    // jwt 토큰이 만료됐는지 확인
    public static boolean checkExpired(String token){
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build()
                .parseClaimsJws(token);

        return claimsJws.getBody().getExpiration().before(new Date());
    }
}
