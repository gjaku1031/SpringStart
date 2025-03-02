package com.example.springstart.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
