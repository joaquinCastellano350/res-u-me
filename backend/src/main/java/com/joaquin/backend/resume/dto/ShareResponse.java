package com.joaquin.backend.resume.dto;

import jakarta.annotation.Nullable;

import java.time.Instant;

public record ShareResponse(
        String token,
        @Nullable Instant expiresAt
        ) { }
