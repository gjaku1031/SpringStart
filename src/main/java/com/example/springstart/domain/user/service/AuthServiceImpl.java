package com.example.springstart.domain.user.service;

import com.example.springstart.domain.user.dto.JoinRequestDto;
import com.example.springstart.domain.user.dto.LoginRequestDto;
import com.example.springstart.domain.user.dto.TokenResponseDto;
import com.example.springstart.domain.user.entity.User;
import com.example.springstart.domain.user.entity.UserRoleType;
import com.example.springstart.domain.user.jwt.JwtTokenProvider;
import com.example.springstart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void join(JoinRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();

        if (userRepository.existsByUsername(username)) {
            return;
        }

        if (password.equals(confirmPassword)) {
            throw new IllegalArgumentException("New passwords don't match");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .role(UserRoleType.USER)
                .email(dto.getEmail())
                .build();
        userRepository.save(user);
    }

    @Override
    public TokenResponseDto login(LoginRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다");
        }

        return new TokenResponseDto(
                jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().toString()),
                jwtTokenProvider.createRefreshToken(user.getUsername())
        );
    }

    @Override
    public void logout(String bearerToken) {
        String accessToken = jwtTokenProvider.resolveToken(bearerToken)
                .orElseThrow(() -> new IllegalArgumentException("Token is null"));

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        jwtTokenProvider.addBlacklist(accessToken);
        jwtTokenProvider.deleteRefreshToken(accessToken);
    }

    @Override
    public TokenResponseDto refresh(String bearerToken) {
        String refreshToken = jwtTokenProvider.resolveToken(bearerToken)
                .orElseThrow(() -> new IllegalArgumentException("Token is null"));

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        User user = userRepository.findByUsername(jwtTokenProvider.getUserName(refreshToken)).get();

        return new TokenResponseDto(
                jwtTokenProvider.createAccessToken(user.getUsername(),
                                                   user.getRole().toString()),
                                                   refreshToken
        );
    }
}