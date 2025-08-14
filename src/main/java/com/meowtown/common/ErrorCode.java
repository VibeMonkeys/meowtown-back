package com.meowtown.common;

public enum ErrorCode {
    
    // Authentication related (4000번대)
    AUTH_TOKEN_MISSING("AUTH_TOKEN_MISSING", "인증 토큰이 없습니다"),
    AUTH_TOKEN_INVALID("AUTH_TOKEN_INVALID", "유효하지 않은 토큰입니다"),
    AUTH_TOKEN_EXPIRED("AUTH_TOKEN_EXPIRED", "만료된 토큰입니다"),
    AUTH_PERMISSION_DENIED("AUTH_PERMISSION_DENIED", "권한이 없습니다"),
    AUTH_LOGIN_FAILED("AUTH_LOGIN_FAILED", "로그인에 실패했습니다"),
    
    // User related (4100번대)
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다"),
    USER_EMAIL_EXISTS("USER_EMAIL_EXISTS", "이미 존재하는 이메일입니다"),
    USER_USERNAME_EXISTS("USER_USERNAME_EXISTS", "이미 존재하는 사용자명입니다"),
    USER_INVALID_CREDENTIALS("USER_INVALID_CREDENTIALS", "잘못된 인증 정보입니다"),
    
    // Cat related (4200번대)
    CAT_NOT_FOUND("CAT_NOT_FOUND", "고양이를 찾을 수 없습니다"),
    CAT_ALREADY_EXISTS("CAT_ALREADY_EXISTS", "이미 등록된 고양이입니다"),
    CAT_UNAUTHORIZED("CAT_UNAUTHORIZED", "고양이 수정/삭제 권한이 없습니다"),
    CAT_IMAGE_LIMIT_EXCEEDED("CAT_IMAGE_LIMIT_EXCEEDED", "이미지 개수 제한을 초과했습니다"),
    CAT_INVALID_COORDINATES("CAT_INVALID_COORDINATES", "유효하지 않은 좌표입니다"),
    
    // File related (4300번대)
    FILE_TOO_LARGE("FILE_TOO_LARGE", "파일 크기가 너무 큽니다"),
    FILE_INVALID_TYPE("FILE_INVALID_TYPE", "지원하지 않는 파일 형식입니다"),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다"),
    
    // General errors (4900-5000번대)
    VALIDATION_ERROR("VALIDATION_ERROR", "유효성 검사 오류입니다"),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", "요청 제한을 초과했습니다"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다"),
    
    // Sighting related (4400번대)
    SIGHTING_NOT_FOUND("SIGHTING_NOT_FOUND", "목격 제보를 찾을 수 없습니다"),
    SIGHTING_UNAUTHORIZED("SIGHTING_UNAUTHORIZED", "목격 제보 수정/삭제 권한이 없습니다"),
    
    // Post related (4500번대)
    POST_NOT_FOUND("POST_NOT_FOUND", "게시글을 찾을 수 없습니다"),
    POST_UNAUTHORIZED("POST_UNAUTHORIZED", "게시글 수정/삭제 권한이 없습니다"),
    
    // Comment related (4600번대)
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다"),
    COMMENT_UNAUTHORIZED("COMMENT_UNAUTHORIZED", "댓글 수정/삭제 권한이 없습니다"),
    
    // Notification related (4700번대)
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}