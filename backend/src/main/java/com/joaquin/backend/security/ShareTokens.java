package com.joaquin.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class ShareTokens {
    private static final SecureRandom RNG = new SecureRandom();

    public static String generateToken() {
        byte[] bytes = new byte[24];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(dig);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 failed", e);
        }
    }


    public static boolean slowEquals(String expectedHash, String token){
        if (expectedHash == null || token == null) return false;
        var exp = Base64.getDecoder().decode(expectedHash);
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var got = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(exp, got);
        }catch (Exception e){
            return false;
        }

    }

    private ShareTokens() {}
}
