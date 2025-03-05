package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.PasswordUpdateRequestDto;
import com.example.springstart.domain.user.dto.PasswordUpdateResponseDto;
import com.example.springstart.domain.user.dto.UserUpdateRequestDto;
import com.example.springstart.domain.user.dto.UserUpdateResponseDto;
import com.example.springstart.domain.user.entity.User;

public interface UserService {

    public UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto dto);

    public void deleteUser(String username);

    public PasswordUpdateResponseDto updatePassword(PasswordUpdateRequestDto dto);
}
