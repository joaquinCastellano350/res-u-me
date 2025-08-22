package com.joaquin.backend.resume.dto;

import java.time.Instant;
import java.util.UUID;

public record ResumeResponse(
        UUID id,
        String originalFilename,
        String mimeType,
        long sizeBytes,
        String storageKey,
        Instant uploadedAt
) {
}
