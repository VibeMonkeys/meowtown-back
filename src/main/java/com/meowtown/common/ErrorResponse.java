package com.meowtown.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private boolean success = false;
    private ErrorDetail error;
    private LocalDateTime timestamp;
    
    public ErrorResponse(String code, String message) {
        this.error = new ErrorDetail(code, message, null, null);
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String code, String message, String details) {
        this.error = new ErrorDetail(code, message, details, null);
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String code, String message, String details, String field) {
        this.error = new ErrorDetail(code, message, details, field);
        this.timestamp = LocalDateTime.now();
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {
        private String code;
        private String message;
        private String details;
        private String field;
    }
}