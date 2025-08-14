package com.meowtown.util;

import com.meowtown.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {
    
    /**
     * 세션에서 현재 사용자 조회
     */
    public static User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
    
    /**
     * 세션에서 사용자 ID 조회
     */
    public static String getCurrentUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }
    
    /**
     * 사용자 로그인 여부 확인
     */
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
    
    /**
     * 세션에 사용자 정보 저장
     */
    public static void setUser(HttpSession session, User user) {
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("user", user);
    }
    
    /**
     * 세션에서 사용자 정보 제거
     */
    public static void clearUser(HttpSession session) {
        session.removeAttribute("userId");
        session.removeAttribute("user");
    }
}