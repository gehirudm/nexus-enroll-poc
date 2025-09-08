package com.nexus.enrollment.common.service;

/**
 * Standardized response wrapper for inter-service communication
 */
public class ServiceResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;
    
    private ServiceResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }
    
    /**
     * Create a successful response with data
     */
    public static <T> ServiceResponse<T> success(T data) {
        return new ServiceResponse<>(true, "Success", data, null);
    }
    
    /**
     * Create a successful response with custom message and data
     */
    public static <T> ServiceResponse<T> success(String message, T data) {
        return new ServiceResponse<>(true, message, data, null);
    }
    
    /**
     * Create an error response with message
     */
    public static <T> ServiceResponse<T> error(String message) {
        return new ServiceResponse<>(false, message, null, null);
    }
    
    /**
     * Create an error response with message and error code
     */
    public static <T> ServiceResponse<T> error(String message, String errorCode) {
        return new ServiceResponse<>(false, message, null, errorCode);
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public boolean isError() {
        return !success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public T getData() {
        return data;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return "ServiceResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
