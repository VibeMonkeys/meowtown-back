package com.meowtown.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String userId;
    private String displayName;
    private String email;
    
    /**
     * JWT 토큰에서 얻을 수 있는 기본 사용자 정보 생성
     */
    public static UserInfo fromJwt(String userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setDisplayName(userId + "_display");
        userInfo.setEmail(userId + "@example.com");
        return userInfo;
    }
    
    /**
     * 테스트용 사용자 정보 생성
     */
    public static UserInfo testUser(String userId) {
        return new UserInfo(userId, "Test " + userId, userId + "@test.com");
    }
}