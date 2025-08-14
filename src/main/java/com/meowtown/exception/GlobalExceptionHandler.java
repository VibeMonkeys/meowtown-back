package com.meowtown.exception;

import com.meowtown.common.ErrorCode;
import com.meowtown.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MeowtownException.class)
    public ResponseEntity<ErrorResponse> handleMeowtownException(MeowtownException e) {
        log.error("MeowtownException: {}", e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails()
        );
        
        HttpStatus status = getHttpStatus(e.getErrorCode());
        return ResponseEntity.status(status).body(response);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.error("ValidationException: {}", e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getField()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
        
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errors
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException: {}", e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
                ErrorCode.FILE_TOO_LARGE.getCode(),
                ErrorCode.FILE_TOO_LARGE.getMessage()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                e.getMessage()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected exception: {}", e.getMessage(), e);
        
        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case AUTH_TOKEN_MISSING, AUTH_TOKEN_INVALID, AUTH_TOKEN_EXPIRED, AUTH_LOGIN_FAILED -> HttpStatus.UNAUTHORIZED;
            case AUTH_PERMISSION_DENIED, CAT_UNAUTHORIZED, SIGHTING_UNAUTHORIZED, 
                 POST_UNAUTHORIZED, COMMENT_UNAUTHORIZED -> HttpStatus.FORBIDDEN;
            case USER_NOT_FOUND, CAT_NOT_FOUND, SIGHTING_NOT_FOUND, POST_NOT_FOUND, 
                 COMMENT_NOT_FOUND, NOTIFICATION_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case USER_EMAIL_EXISTS, USER_USERNAME_EXISTS, CAT_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}