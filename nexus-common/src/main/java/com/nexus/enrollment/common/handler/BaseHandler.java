package com.nexus.enrollment.common.handler;

import com.nexus.enrollment.common.util.ResponseBuilder;

public abstract class BaseHandler {
    
    // Utility methods for creating JSON responses
    protected Object createSuccessResponse(String message, Object data) {
        return new ResponseWrapper("success", message, data);
    }
    
    protected Object createErrorResponse(String message) {
        return new ResponseWrapper("error", message, null);
    }
    
    // Convert ResponseBuilder.Response to our standard format
    protected Object convertResponse(ResponseBuilder.Response response) {
        if (response.isSuccess()) {
            return createSuccessResponse(response.getMessage(), response.getData());
        } else {
            return createErrorResponse(response.getMessage());
        }
    }
    
    // Simple response wrapper class
    @SuppressWarnings("unused") // Fields are used by JSON serialization
    public static class ResponseWrapper {
        public final String status;
        public final String message;
        public final Object data;
        
        public ResponseWrapper(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }
}
