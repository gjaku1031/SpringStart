package com.example.springstart.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
