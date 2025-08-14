package com.meowtown.service;

import com.meowtown.common.ErrorCode;
import com.meowtown.dto.request.auth.LoginRequestDto;
import com.meowtown.dto.request.auth.RegisterRequestDto;
import com.meowtown.dto.response.auth.LoginResponseDto;
import com.meowtown.dto.response.user.UserResponseDto;
import com.meowtown.entity.User;
import com.meowtown.entity.enums.UserRole;
import com.meowtown.exception.MeowtownException;
import com.meowtown.repository.UserRepository;
import com.meowtown.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    
    public UserResponseDto register(RegisterRequestDto request) {
        validateRegistration(request);
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .phoneNumber(request.getPhoneNumber())
                .location(request.getLocation())
                .role(UserRole.USER)
                .isVerified(false)
                .build();
        
        User savedUser = userRepository.save(user);
        return userService.convertToResponseDto(savedUser);
    }
    
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        String accessToken = jwtTokenProvider.createToken(authentication.getName(), authorities);
        String refreshToken = jwtTokenProvider.createToken(authentication.getName(), authorities);
        
        User user = userRepository.findByUsername(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new MeowtownException(ErrorCode.USER_NOT_FOUND));
        
        UserResponseDto userResponse = userService.convertToResponseDto(user);
        
        return new LoginResponseDto(accessToken, refreshToken, userResponse);
    }
    
    private void validateRegistration(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new MeowtownException(ErrorCode.USER_USERNAME_EXISTS, "사용자명: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new MeowtownException(ErrorCode.USER_EMAIL_EXISTS, "이메일: " + request.getEmail());
        }
    }
}