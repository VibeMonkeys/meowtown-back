package com.meowtown.exception;

import com.meowtown.common.ErrorCode;

public class ValidationException extends MeowtownException {
    
    private final String field;
    
    public ValidationException(ErrorCode errorCode, String field) {
        super(errorCode);
        this.field = field;
    }
    
    public ValidationException(ErrorCode errorCode, String details, String field) {
        super(errorCode, details);
        this.field = field;
    }
    
    public String getField() {
        return field;
    }
}