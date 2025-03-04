package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.PasswordUpdateRequestDto;
import com.example.springstart.domain.user.dto.PasswordUpdateResponseDto;
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

    @Override
    public UserUpdateResponseDto updateUser(Long id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateUser(dto.getUsername(), dto.getEmail());

        userRepository.save(user);
        return new UserUpdateResponseDto(user);
    }

    @Override
    public PasswordUpdateResponseDto updatePassword(Long id, PasswordUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords don't match");
        }

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.updatePassword(encodedPassword);

        userRepository.save(user);

        return new PasswordUpdateResponseDto(); //비밀번호 body에 노출 위험 -> 메시지 반환
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
    }
}