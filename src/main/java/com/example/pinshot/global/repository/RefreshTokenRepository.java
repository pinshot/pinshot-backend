package com.example.pinshot.global.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RefreshTokenRepository {
    private final Map<String, String> refreshTokenStore = new HashMap<>();

    public void saveRefreshToken(String phoneNumber, String refreshToken) {
        refreshTokenStore.put(phoneNumber, refreshToken);
    }

    public String findPhoneNumberByRefreshToken(String refreshToken) {
        return refreshTokenStore.getOrDefault(refreshToken, null);
    }

    public void deleteRefreshToken(String phoneNumber) {
        refreshTokenStore.remove(phoneNumber);
    }
}
