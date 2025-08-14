package com.meowtown.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("male"),
    FEMALE("female"), 
    UNKNOWN("unknown");
    
    private final String value;
    
    Gender(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) return UNKNOWN;
        
        switch (value.toLowerCase()) {
            case "male": return MALE;
            case "female": return FEMALE;
            case "unknown": return UNKNOWN;
            default: return UNKNOWN;
        }
    }
}