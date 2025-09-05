package com.nexus.enrollment.common.exceptions;

public class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }
    
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
