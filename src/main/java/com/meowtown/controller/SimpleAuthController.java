package com.meowtown.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3001")
public class SimpleAuthController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "AuthController is working!");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        // 사용자 데이터 구조 생성
        Map<String, Object> user = new HashMap<>();
        user.put("userId", request.get("userId"));
        user.put("email", request.get("email"));
        user.put("displayName", request.get("displayName"));
        
        // 응답 데이터 구조
        data.put("token", "temp-token-" + System.currentTimeMillis());
        data.put("tokenType", "Bearer");
        data.put("user", user);
        
        response.put("success", true);
        response.put("data", data);
        response.put("message", "회원가입이 완료되었습니다!");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        // 사용자 데이터 구조 생성 (실제로는 DB에서 조회)
        Map<String, Object> user = new HashMap<>();
        user.put("userId", request.get("userId"));
        user.put("email", "user@example.com"); // 임시 이메일
        user.put("displayName", "사용자"); // 임시 닉네임
        
        // 응답 데이터 구조
        data.put("token", "temp-token-" + System.currentTimeMillis());
        data.put("tokenType", "Bearer");
        data.put("user", user);
        
        response.put("success", true);
        response.put("data", data);
        response.put("message", "로그인이 완료되었습니다!");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}