package com.example.springstart.domain.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * JWT 인증 필터
 * 모든 요청에서 실행되며, JWT 토큰을 검사하여 유효한 경우 SecurityContext에 인증 정보를 저장한다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * HTTP 요청이 들어올 때마다 실행되는 필터 메서드
     * JWT 토큰을 검증하고, 유효한 경우 SecurityContext에 저장한다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인 (다음 필터로 요청을 넘기기 위해 사용)
     * @throws ServletException 서블릿 예외 발생 시
     * @throws IOException 입출력 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. HTTP 요청 헤더에서 Authorization 값을 가져와 JWT 토큰을 안전하게 추출
        Optional<String> optionalToken = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

        // 2. 토큰이 존재하고 유효한 경우, SecurityContext에 Authentication 객체를 설정
        optionalToken.filter(this::isUsableAccessToken)
                .map(jwtTokenProvider::getAuthentication)
                .ifPresent(authentication -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✅ 사용자 인증 완료: {}", authentication.getName());
                });

        // 3. 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 액세스 토큰이 유효한지 확인하는 메서드
     * - 토큰이 존재하고
     * - 유효하며 (만료되지 않음)
     * - 블랙리스트에 등록되지 않았으며 (로그아웃된 토큰이 아님)
     * - 역할(Role) 정보가 포함되어 있는지 확인
     *
     * @param token 확인할 JWT 토큰
     * @return 유효한 액세스 토큰이면 true, 그렇지 않으면 false
     */
    private boolean isUsableAccessToken(String token) {
        boolean isValid = jwtTokenProvider.validateToken(token)
                && !jwtTokenProvider.isBlacklisted(token)
                && jwtTokenProvider.hasRole(token);

        if (!isValid) {
            log.warn("⚠️ 사용 불가능한 토큰 감지: {}", token);
        }

        return isValid;
    }
}
