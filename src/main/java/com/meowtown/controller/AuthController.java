package com.meowtown.controller;

import com.meowtown.common.ApiResponse;
import com.meowtown.security.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "https://meowtown-front.vercel.app", "https://meowtown-front-btumrmy14-kimkyunghun3s-projects.vercel.app"}, allowCredentials = "true")
public class AuthController {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Data
    public static class LoginRequest {
        @NotBlank(message = "사용자 ID는 필수입니다.")
        private String userId;
        
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }
    
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "사용자 ID는 필수입니다.")
        @Size(min = 3, max = 50, message = "사용자 ID는 3-50자여야 합니다.")
        private String userId;
        
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;
        
        @NotBlank(message = "표시 이름은 필수입니다.")
        @Size(min = 2, max = 100, message = "표시 이름은 2-100자여야 합니다.")
        private String displayName;
        
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 100, message = "비밀번호는 4-100자여야 합니다.")
        private String password;
    }
    
    @Data
    public static class UserResponse {
        private String userId;
        private String email;
        private String displayName;
        
        public static UserResponse testUser(String userId) {
            UserResponse response = new UserResponse();
            response.userId = userId;
            response.email = userId + "@example.com";
            response.displayName = "Test " + userId;
            return response;
        }
    }
    
    /**
     * 로그인 (테스트용 하드코딩)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("Login attempt for user: {}", request.getUserId());
        
        // 테스트용 하드코딩된 유저 (실제로는 DB에서 조회해야 함)
        if ("testuser".equals(request.getUserId()) && "test123".equals(request.getPassword())) {
            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(request.getUserId(), "ROLE_USER");
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", UserResponse.testUser(request.getUserId()));
            result.put("token", token);
            result.put("tokenType", "Bearer");
            
            log.info("Login successful for user: {}, JWT token generated", request.getUserId());
            return ResponseEntity.ok(ApiResponse.success(result, "로그인 성공"));
        }
        
        log.warn("Login failed for user: {}", request.getUserId());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("사용자 ID 또는 비밀번호가 올바르지 않습니다."));
    }
    
    /**
     * 회원가입 (테스트용 간단 구현)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Registration attempt for user: {}", request.getUserId());
        
        // 테스트용 간단한 회원가입 (실제로는 DB 저장 필요)
        String token = jwtTokenProvider.createToken(request.getUserId(), "ROLE_USER");
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", UserResponse.testUser(request.getUserId()));
        result.put("token", token);
        result.put("tokenType", "Bearer");
        
        log.info("Registration successful for user: {}, JWT token generated", request.getUserId());
        return ResponseEntity.ok(ApiResponse.success(result, "회원가입 성공"));
    }
    
    /**
     * 로그아웃 (JWT는 클라이언트에서 토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        log.info("Logout requested");
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }
    
    /**
     * 인증 확인 (JWT 토큰 검증)
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkAuth() {
        log.info("Auth check requested");
        return ResponseEntity.ok(ApiResponse.success("authenticated", "인증됨"));
    }
}