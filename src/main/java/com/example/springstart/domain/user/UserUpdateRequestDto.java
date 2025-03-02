package com.example.springstart.domain.user;

import jakarta.validation.constraints.NotBlank;

public class UserUpdateRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
