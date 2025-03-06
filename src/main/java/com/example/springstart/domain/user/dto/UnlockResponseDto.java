package com.example.springstart.domain.user.dto;

import lombok.Data;

@Data
public class UnlockResponseDto {
    String message;

    public UnlockResponseDto() {
        this.message = "unlocked";
    }

}
