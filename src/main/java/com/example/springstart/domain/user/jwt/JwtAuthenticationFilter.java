package com.example.springstart.domain.user.jwt;

import com.example.springstart.domain.user.dto.CustomUserDetails;
import com.example.springstart.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JWT 인증 필터
 * 모든 요청에서 실행되며, JWT 토큰을 검사하여 유효한 경우 SecurityContext에 인증 정보를 저장한다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 "Authorization" 값을 추출하여 Optional<String>에 저장합니다.
        //    (예: "Bearer <token>")
        Optional<String> optionalToken = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

        // 2. 토큰이 존재하면, 아래 조건들을 만족하는지 확인합니다:
        //    - 토큰이 유효한지 (만료 여부, 서명 검증 등)
        //    - 토큰이 블랙리스트에 있지 않은지
        //    - 토큰에 역할(Role) 정보가 포함되어 있는지 등
        //    조건을 만족하면, jwtTokenProvider의 getAuthentication 메서드를 사용해
        //    Authentication 객체를 생성하고 이를 Optional<Authentication>으로 반환합니다.
        Optional<Authentication> authOpt = optionalToken
                .filter(this::isUsableAccessToken)  // 토큰 유효성 검사: 조건이 false이면 Optional.empty()가 됩니다.
                .map(jwtTokenProvider::getAuthentication);  // 토큰이 유효하면 Authentication 객체로 매핑

        // 3. 인증 정보(Authentication 객체)가 존재하면, SecurityContextHolder에 설정합니다.
        //    이렇게 하면 이후 요청에서 스프링 시큐리티가 인증된 사용자로 인식하게 됩니다.
        authOpt.ifPresent(authentication -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // CustomUserDetails는 사용자의 상세 정보를 담은 객체입니다.
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            log.info("✅ 사용자 인증 완료: {}", userDetails);
        });

        // 4. Optional을 이용하여 CustomUserDetails 객체를 추출합니다.
        //    만약 authOpt가 empty면, null이 반환됩니다.
        CustomUserDetails auth = authOpt
                .map(authentication -> (CustomUserDetails) authentication.getPrincipal())
                .orElse(null);

        // 5. 만약 인증된 사용자가 존재한다면 추가 로직(예: 차단 여부 확인)을 수행합니다.
        if (auth != null) {
            System.out.println("auth = " + auth);

            // isEnabled()는 CustomUserDetails에서 구현된 메서드로,
            // 사용자의 차단 여부에 따라 false를 반환합니다.
            if (!auth.isEnabled()) {
                log.info("밴 당한 사용자 입니다");
                // 여기서 추가 처리가 가능: 예를 들어, 응답에 403 Forbidden을 반환하거나 추가 로깅 수행
            }
        }

        // 6. 다음 필터로 요청을 전달합니다.
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
        log.info("JWT Authentication Filter - isUsableAccessToken");
        boolean isValid = jwtTokenProvider.validateToken(token)
                && !jwtTokenProvider.isBlacklisted(token)
                && jwtTokenProvider.hasRole(token);

        if (!isValid) {
            log.warn("⚠️ 사용 불가능한 토큰 감지: {}", token);
        }

        return isValid;
    }
}