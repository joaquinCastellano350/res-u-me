package com.joaquin.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record SignupRequest(
        @Size(min = 3, max = 128) String fullname,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 128) String password
) { }
