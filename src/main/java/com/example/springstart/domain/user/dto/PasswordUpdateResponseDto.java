package com.example.springstart.domain.user.dto;

import com.example.springstart.domain.user.entity.User;

public class PasswordUpdateResponseDto {
    String message;

    public PasswordUpdateResponseDto() {
        this.message = "Password updated successfully";
    }
}
