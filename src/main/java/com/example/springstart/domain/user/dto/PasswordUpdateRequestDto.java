package com.example.springstart.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordUpdateRequestDto {

    @NotBlank
    @Size(min = 2, max = 20)
    String currentPassword;

    @NotBlank
    @Size(min = 2, max = 20)
    String newPassword;

    @NotBlank
    @Size(min = 2, max = 20)
    String confirmPassword;

}