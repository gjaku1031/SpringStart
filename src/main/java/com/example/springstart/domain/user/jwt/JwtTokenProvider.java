package com.example.springstart.domain.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * JWT 토큰을 생성, 검증, 관리하는 클래스
 * - 액세스 토큰 & 리프레시 토큰 생성
 * - 토큰 검증 및 파싱
 * - 블랙리스트 관리 (로그아웃 처리)
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    // 액세스 토큰 유효기간 (15분)
    private static final long ACCESS_TOKEN_EXP = 1000L * 60L * 15L;

    // 리프레시 토큰 유효기간 (1일)
    private static final long REFRESH_TOKEN_EXP = 1000L * 60L * 60L * 24;

    /**
     * 생성자: JWT 비밀키를 설정하고, 필요한 서비스들을 주입받음
     *
     * @param secret            JWT 서명용 비밀키 (application.yml에서 설정)
     * @param userDetailsService 사용자 정보를 가져오는 서비스
     * @param redisTemplate     Redis에 토큰을 저장/관리하기 위한 템플릿
     */
    public JwtTokenProvider(
            @Value("${springboot.jwt.secret}") String secret,
            UserDetailsService userDetailsService,
            RedisTemplate<String, String> redisTemplate) {

        log.debug("Secret: {}", secret);

        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    // ========================= 🔹 토큰 생성 관련 메서드 =========================

    /**
     * 액세스 토큰을 생성하는 메서드
     *
     * @param username 사용자 이름
     * @param role     사용자 역할 (권한)
     * @return 생성된 액세스 토큰
     */
    public String createAccessToken(String username, String role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);

        return createToken(claims, ACCESS_TOKEN_EXP);
    }

    /**
     * 리프레시 토큰을 생성하는 메서드
     *
     * @param username 사용자 이름
     * @return 생성된 리프레시 토큰
     */
    public String createRefreshToken(String username) {
        Map<String, String> claims = Map.of("username", username);
        String refreshToken = createToken(claims, REFRESH_TOKEN_EXP);

        // 리프레시 토큰을 Redis에 저장 (유효기간을 설정하여 자동 만료 처리)
        redisTemplate.opsForValue().set("refresh:" + username, refreshToken, REFRESH_TOKEN_EXP, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    /**
     * JWT 토큰을 생성하는 내부 메서드
     *
     * @param claims   JWT에 포함할 정보 (Payload)
     * @param tokenExp 토큰 유효기간
     * @return 생성된 JWT 문자열
     */
    private String createToken(Map<String, String> claims, long tokenExp) {
        return Jwts.builder()
                .header().add("typ", "JWT").and() // JWT 헤더에 typ 추가
                .claims(claims) // 클레임 추가
                .id(Long.toHexString(System.nanoTime())) // JWT ID (jti) 설정
                .issuedAt(new Date()) // 발급 시간 설정
                .expiration(new Date(System.currentTimeMillis() + tokenExp)) // 만료 시간 설정
                .signWith(secretKey) // 서명 생성
                .compact(); // JWT 토큰을 생성
    }

    // ========================= 🔹 토큰 검증 및 조회 관련 메서드 =========================

    /**
     * 토큰이 유효한지 검증하는 메서드
     *
     * @param token 검증할 JWT
     * @return 유효하면 true, 만료되었으면 false 반환
     */
    public boolean validateToken(String token) {
        return !getClaims(token).getExpiration().before(new Date());
    }

    /**
     * JWT에서 사용자 이름을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUserName(String token) {
        return getClaims(token).get("username").toString();
    }

    /**
     * JWT에서 JTI (JWT ID) 값을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return JTI 값
     */
    private String getJti(String token) {
        return getClaims(token).getId();
    }

    /**
     * 토큰에 역할(Role)이 포함되어 있는지 확인하는 메서드
     *
     * @param token JWT 토큰
     * @return 역할 정보가 존재하면 true, 없으면 false
     */
    public boolean hasRole(String token) {
        return getClaims(token).get("role") != null;
    }

    /**
     * JWT를 파싱하여 클레임을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return Claims 객체
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ========================= 🔹 인증(Authentication) 관련 메서드 =========================

    /**
     * 요청에서 전달된 Bearer 토큰을 추출하는 메서드
     *
     * @param bearerToken HTTP 요청 헤더에서 전달된 토큰
     * @return 실제 JWT 토큰 (Bearer 제거)
     */
    public Optional<String> resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    /**
     * 토큰을 이용해 Authentication 객체를 생성하는 메서드
     *
     * @param token JWT 토큰
     * @return Spring Security의 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        String username = getUserName(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // ========================= 🔹 블랙리스트 및 토큰 관리 =========================

    public void addBlacklist(String accessToken) {
        redisTemplate.opsForValue().set("blacklist:" + getJti(accessToken), "true", ACCESS_TOKEN_EXP, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + getJti(token));
    }

    public void deleteRefreshToken(String accessToken) {
        redisTemplate.delete("refresh:" + getUserName(accessToken));
    }

    public boolean isValidRefreshToken(String refreshToken) {
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + getUserName(refreshToken));
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
    }
}