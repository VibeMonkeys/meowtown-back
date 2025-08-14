package com.meowtown.service;

import com.meowtown.entity.User;
import com.meowtown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    
    /**
     * 회원가입
     */
    public User register(String userId, String email, String displayName, String password) {
        // 중복 체크
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        // 사용자 생성
        User user = User.builder()
                .userId(userId)
                .email(email)
                .displayName(displayName)
                .password(password) // 간단히 평문 저장
                .active(true)
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * 로그인
     */
    public Optional<User> login(String userId, String password) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 비밀번호 확인 (간단히 평문 비교)
            if (password.equals(user.getPassword()) && user.isActive()) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * 사용자 조회
     */
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
}