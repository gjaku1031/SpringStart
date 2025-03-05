package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.*;
import com.example.springstart.domain.user.entity.User;


public interface AuthService {
    void join(JoinRequestDto dto);

    TokenResponseDto login(LoginRequestDto dto);

    void logout(String bearerToken);

    TokenResponseDto refresh(String bearerToken);

    public BanResponseDto banUser(BanRequestDto dto);
}
