package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.JoinRequestDto;
import com.example.springstart.domain.user.dto.LoginRequestDto;
import com.example.springstart.domain.user.dto.TokenResponseDto;
import com.example.springstart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface UserService {
    void join(JoinRequestDto dto);

    TokenResponseDto login(LoginRequestDto dto);

    void logout(String bearerToken);

    TokenResponseDto refresh(String bearerToken);
}
