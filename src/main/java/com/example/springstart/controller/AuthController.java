package com.example.springstart.controller;

import com.example.springstart.domain.user.dto.LoginRequestDto;
import com.example.springstart.domain.user.dto.TokenResponseDto;
import com.example.springstart.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 및 토큰 관련 API를 제공하는 컨트롤러.
 * - 로그인 (Access Token, Refresh Token 발급)
 * - 로그아웃 (토큰 무효화)
 * - 액세스 토큰 재발급 (Refresh Token 사용)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")  // 모든 엔드포인트가 /api/v1/auth 로 시작
@RequiredArgsConstructor
//@Tag(name = "Auth APIs", description = "인증 관련 API 목록")
public class AuthController {

    // 인증 관련 비즈니스 로직을 처리하는 서비스 (DI 주입)
    private final AuthService authService;

    /**
     * 로그인 API
     * - 사용자 아이디와 패스워드를 받아 검증 후, JWT (Access Token, Refresh Token)를 반환한다.
     * - Refresh Token은 보통 클라이언트에서 저장하고, 이후 액세스 토큰 재발급 요청에 사용된다.
     *
     * @param loginRequestDto 로그인 요청 데이터 (username, password 포함)
     * @return TokenResponseDto (Access Token, Refresh Token 포함)
     */
    @PostMapping("/login")
/*    @Operation(summary = "로그인", description = "아이디와 패스워드를 JSON으로 받아서 로그인한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK - 로그인 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED - 로그인 실패 (잘못된 아이디 또는 비밀번호)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL SERVER ERROR - 서버 에러",
                    content = @Content(mediaType = "application/json")
            )
    })*/
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("로그인 요청: {}", loginRequestDto.getUsername());

        // AuthService를 이용하여 로그인 처리 및 토큰 발급
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto);

        log.info("로그인 성공 - 사용자: {}", loginRequestDto.getUsername());

        return ResponseEntity.ok(tokenResponseDto);
    }

    /**
     * 로그아웃 API
     * - 클라이언트가 보낸 액세스 토큰을 무효화 처리.
     * - 실제 구현에서는 서버 측에서 JWT를 강제 만료시킬 수 없기 때문에,
     *   일반적으로 블랙리스트 처리하거나, 클라이언트 측에서 토큰을 삭제하도록 유도한다.
     *
     * @param bearerToken Authorization 헤더에서 전달된 액세스 토큰 (예: "Bearer xxxxx")
     * @return HTTP 204 (No Content) - 성공적으로 로그아웃됨
     */
    @PostMapping("/logout")
/*    @Operation(summary = "로그아웃", description = "Access Token을 전달받아 로그아웃 처리.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "NO CONTENT - 로그아웃 성공"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL SERVER ERROR - 서버 에러"
            )
    })*/
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        log.info("로그아웃 요청 - 토큰: {}", bearerToken);

        // AuthService를 이용하여 로그아웃 처리 (토큰 블랙리스트 처리 등)
        authService.logout(bearerToken);

        log.info("로그아웃 성공");

        return ResponseEntity.noContent().build();  // HTTP 204 반환 (응답 본문 없음)
    }

    /**
     * 액세스 토큰 재발급 API
     * - 기존 액세스 토큰이 만료되었을 때, Refresh Token을 사용하여 새로운 액세스 토큰을 발급한다.
     * - 클라이언트는 로그인 후 받은 Refresh Token을 사용하여 이 API를 호출해야 한다.
     * - Refresh Token이 유효하지 않거나 만료된 경우, 401 응답을 반환한다.
     *
     * @param bearerToken Authorization 헤더에서 전달된 Refresh Token (예: "Bearer xxxxx")
     * @return 새로운 Access Token (JSON 응답)
     */
    @PostMapping("/refresh")
/*    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용하여 새로운 Access Token을 발급.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK - 새 액세스 토큰 발급 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED - Refresh Token이 유효하지 않음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL SERVER ERROR - 서버 에러",
                    content = @Content(mediaType = "application/json")
            )
    })*/
    public ResponseEntity<TokenResponseDto> refresh(@RequestHeader("Authorization") String bearerToken) {
        log.info("토큰 재발급 요청 - Refresh Token: {}", bearerToken);

        // AuthService를 이용하여 새로운 액세스 토큰을 발급받음
        TokenResponseDto tokenResponseDto = authService.refresh(bearerToken);

        log.info("토큰 재발급 성공 - 새로운 액세스 토큰 발급됨");

        return ResponseEntity.ok(tokenResponseDto);
    }
}
