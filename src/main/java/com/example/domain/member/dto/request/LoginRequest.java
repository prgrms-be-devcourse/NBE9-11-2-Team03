package com.example.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// Login Request DTO
public class LoginRequest {

    @NotBlank(message = "loginId is required.")
    private String loginId;

    @NotBlank(message = "password is required.")
    private String password;
}
