package com.alten.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
    @NotBlank String username,
    @NotBlank String firstname,
    @NotBlank @Email String email,
    @NotBlank String password
) {}