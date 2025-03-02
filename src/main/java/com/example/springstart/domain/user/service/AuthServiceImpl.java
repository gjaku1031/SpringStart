package com.example.springstart.domain.user.service;



import com.example.springstart.domain.user.dto.JoinRequestDto;
import com.example.springstart.domain.user.dto.LoginRequestDto;
import com.example.springstart.domain.user.dto.TokenResponseDto;
import com.example.springstart.domain.user.entity.User;
import com.example.springstart.domain.user.entity.UserRoleType;
import com.example.springstart.domain.user.jwt.JwtTokenProvider;
import com.example.springstart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void join(JoinRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .role(UserRoleType.USER)
                .build();
        userRepository.save(user);
    }

    @Override
    public TokenResponseDto login(LoginRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));

        if(!password.equals(user.getPassword())) {
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