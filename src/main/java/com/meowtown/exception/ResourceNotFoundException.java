package com.meowtown.exception;

import com.meowtown.common.ErrorCode;

public class ResourceNotFoundException extends MeowtownException {
    
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ResourceNotFoundException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }
}