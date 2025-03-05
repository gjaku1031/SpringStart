package com.example.springstart.domain.user.dto;

import lombok.Data;

@Data
public class PasswordUpdateResponseDto {
    String message;

    public PasswordUpdateResponseDto() {
        this.message = "Password updated successfully";
    }
}