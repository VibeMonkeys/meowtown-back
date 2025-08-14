package com.meowtown.service;

import com.meowtown.common.ErrorCode;
import com.meowtown.dto.request.user.UpdateUserRequestDto;
import com.meowtown.dto.response.user.UserResponseDto;
import com.meowtown.entity.User;
import com.meowtown.exception.ResourceNotFoundException;
import com.meowtown.exception.UnauthorizedException;
import com.meowtown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        User user = getCurrentUserEntity();
        return convertToResponseDto(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "ID: " + userId));
        return convertToResponseDto(user);
    }
    
    public UserResponseDto updateCurrentUser(UpdateUserRequestDto request) {
        User user = getCurrentUserEntity();
        
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UnauthorizedException(ErrorCode.USER_EMAIL_EXISTS, "이메일: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        if (StringUtils.hasText(request.getDisplayName())) {
            user.setDisplayName(request.getDisplayName());
        }
        
        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (StringUtils.hasText(request.getLocation())) {
            user.setLocation(request.getLocation());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }
    
    public void updateAvatarUrl(String avatarUrl) {
        User user = getCurrentUserEntity();
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_MISSING, "인증이 필요합니다");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "사용자명: " + username));
    }
    
    @Transactional(readOnly = true)
    public boolean isCurrentUser(UUID userId) {
        try {
            User currentUser = getCurrentUserEntity();
            return currentUser.getId().equals(userId);
        } catch (UnauthorizedException e) {
            return false;
        }
    }
    
    public UserResponseDto convertToResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getPhoneNumber(),
                user.getLocation(),
                user.getIsVerified(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}