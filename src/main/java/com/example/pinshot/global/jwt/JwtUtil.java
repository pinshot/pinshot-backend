package com.example.pinshot.global.jwt;

import com.example.pinshot.global.exception.ErrorCode;
import com.example.pinshot.global.exception.jwt.InvalidJwtTokenTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
    private static final long VERIFYING_TOKEN_EXPIRATION_TIME = 1000L * 60 * 3; // 3분
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // 1주일
    private static final long SIGNUP_TOKEN_EXPIRATION_TIME = 1000L * 60 * 5; // 5분

    public JwtUtil(@Value("${jwt.secret}") String jwtSecretKey) {
        JWT_SECRET_KEY = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    private static Map<String, Object> createClaims(String phoneNumber){
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", phoneNumber);
        return claims;
    }

    private static Map<String, Object> createVerifyingClaims(String phoneNumber, String verifyCode){
        Map<String, Object> verifyingClaims = new HashMap<>();
        verifyingClaims.put("phoneNumber", phoneNumber);
        verifyingClaims.put("verifyCode", verifyCode);
        return verifyingClaims;
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

    // 토큰 타입에 따른 각각의 jwt 토큰 생성 (VerifyingToken 제외)
    public static String generateJwtToken(String phoneNumber, TokenType tokenType) {
        long now = System.currentTimeMillis(); // JWT 토큰 생성 시간
        long expirationTime = switch (tokenType) {
            case ACCESS -> ACCESS_TOKEN_EXPIRATION_TIME;
            case REFRESH ->  REFRESH_TOKEN_EXPIRATION_TIME;
            case SIGNUP -> SIGNUP_TOKEN_EXPIRATION_TIME;
            default -> {
                log.error("잘못된 토큰 타입입니다: {}", tokenType);
                throw new InvalidJwtTokenTypeException(ErrorCode.INVALID_JWT_TOKEN_TYPE);
            }
        };

        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setClaims(createClaims(phoneNumber))
                .setExpiration(new Date(now + expirationTime))
                .signWith(JWT_SECRET_KEY)
                .compact();
    }

    // jwt 토큰에서 전화 번호 추출
    public static String getPhoneNumber(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("phoneNumber", String.class);
    }

    // jwt가 만료됐는지 확인
    public static boolean checkExpired(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return false; // Jwts의 getExpiration()을 통과했다는 것은 jwt가 만료되지 않았다는 뜻이다
        } catch(ExpiredJwtException e){
            return true; // ExpiredJwtException이 발생하면 jwt가 만료되었다는 뜻이다
        }
    }
}
