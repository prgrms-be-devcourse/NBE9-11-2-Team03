package com.example.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// Signup Request DTO
public class SignupRequest {

    @NotBlank(message = "userName is required.")
    private String userName;

    @NotBlank(message = "loginId is required.")
    private String loginId;

    @NotBlank(message = "password is required.")
    private String password;

    @Email(message = "email must be valid.")
    @NotBlank(message = "email is required.")
    private String email;

    @NotBlank(message = "nickname is required.")
    private String nickname;
}
