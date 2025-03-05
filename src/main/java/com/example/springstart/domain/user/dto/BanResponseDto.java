package com.example.springstart.domain.user.dto;

import lombok.Data;

@Data
public class BanResponseDto {
    String message;

    public BanResponseDto() {
        this.message = "Successfully";
    }
}
