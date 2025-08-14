package com.meowtown.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String tokenType;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private String userId;
        private String email;
        private String displayName;
    }
}