package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.UserUpdateRequestDto;
import com.example.springstart.domain.user.dto.UserUpdateResponseDto;
import com.example.springstart.domain.user.entity.User;
import com.example.springstart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String encodedPassword = passwordEncoder.encode(dto.getPassword()); // 비밀번호 암호화
        user.updateUser(dto.getUsername(), encodedPassword); // 변경

        userRepository.save(user);
        return new UserUpdateResponseDto(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
    }
}