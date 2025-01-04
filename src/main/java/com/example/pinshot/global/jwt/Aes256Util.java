package com.example.pinshot.global.jwt;

// 토큰 암호화

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class Aes256Util {
    private static final String alg = "AES/CBC/PKCS5Padding";
    private static final String KEY = "KEYVALUEISFUNSPRINGBOOTG01234567";
    private static final String IV = KEY.substring(0,16);

    // 유저 정보 암호화
    public static String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            return e.toString();
        }
    }

    // 유저 정보 복호화
    public static String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE,keySpec,ivParameterSpec);

            byte[] decodeBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodeBytes);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            return null;
        }
    }


}
