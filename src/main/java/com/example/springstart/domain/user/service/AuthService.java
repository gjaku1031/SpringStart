package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.JoinRequestDto;
import com.example.springstart.domain.user.dto.LoginRequestDto;
import com.example.springstart.domain.user.dto.TokenResponseDto;


public interface AuthService {
    void join(JoinRequestDto dto);

    TokenResponseDto login(LoginRequestDto dto);

    void logout(String bearerToken);

    TokenResponseDto refresh(String bearerToken);
}
