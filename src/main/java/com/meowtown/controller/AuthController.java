package com.meowtown.controller;

import com.meowtown.common.ApiResponse;
import com.meowtown.entity.User;
import com.meowtown.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "https://meowtown-front-yasyjc9vm-kimkyunghun3s-projects.vercel.app"}, allowCredentials = "true")
public class AuthController {
    
    private final AuthService authService;
    
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
        
        public static UserResponse from(User user) {
            UserResponse response = new UserResponse();
            response.userId = user.getUserId();
            response.email = user.getEmail();
            response.displayName = user.getDisplayName();
            return response;
        }
    }
    
    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        
        try {
            Optional<User> userOpt = authService.login(request.getUserId(), request.getPassword());
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // 세션에 사용자 정보 저장
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("user", user);
                
                Map<String, Object> result = new HashMap<>();
                result.put("user", UserResponse.from(user));
                result.put("token", session.getId()); // 세션 ID를 토큰처럼 사용
                result.put("tokenType", "session");
                
                return ResponseEntity.ok(ApiResponse.success(result, "로그인 성공"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("사용자 ID 또는 비밀번호가 올바르지 않습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("로그인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpSession session) {
        
        try {
            User user = authService.register(
                    request.getUserId(),
                    request.getEmail(),
                    request.getDisplayName(),
                    request.getPassword()
            );
            
            // 회원가입 후 자동 로그인
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("user", user);
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", UserResponse.from(user));
            result.put("token", session.getId());
            result.put("tokenType", "session");
            
            return ResponseEntity.ok(ApiResponse.success(result, "회원가입 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회원가입 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }
    
    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        if (user != null) {
            return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
        } else {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증되지 않은 사용자입니다."));
        }
    }
}