package com.joaquin.backend.auth.dto;

public record AuthToken(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
