package common.util;

import common.exceptions.ValidationException;

public class Validator {
    
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be null or empty");
        }
    }
    
    public static void validateEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new ValidationException("Invalid email format");
        }
    }
    
    public static void validatePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }
    
    public static void validateNonNegative(Integer value, String fieldName) {
        if (value == null || value < 0) {
            throw new ValidationException(fieldName + " cannot be negative");
        }
    }
}
