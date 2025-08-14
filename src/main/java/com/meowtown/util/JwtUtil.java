package com.meowtown.util;

import com.meowtown.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * 현재 인증된 사용자 ID 조회
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * 현재 사용자가 로그인했는지 확인
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
    
    /**
     * Request에서 JWT 토큰 추출
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    
    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        try {
            return jwtTokenProvider.getUsernameFromToken(token);
        } catch (Exception e) {
            log.warn("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Request에서 현재 사용자 ID 추출 (토큰 기반)
     */
    public String getCurrentUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            return getUserIdFromToken(token);
        }
        return null;
    }
}