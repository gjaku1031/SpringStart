package com.example.springstart.domain.user.dto;

import com.example.springstart.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateResponseDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public UserUpdateResponseDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
