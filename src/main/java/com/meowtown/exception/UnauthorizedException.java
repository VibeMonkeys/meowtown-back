package com.meowtown.exception;

import com.meowtown.common.ErrorCode;

public class UnauthorizedException extends MeowtownException {
    
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public UnauthorizedException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }
}