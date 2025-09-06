package com.nexus.enrollment.common.exceptions;

import io.javalin.http.Context;
import com.nexus.enrollment.common.util.ResponseBuilder;

/**
 * Exception for invalid number format in HTTP requests (e.g., invalid ID formats)
 */
public class InvalidFormatException extends HttpException {
    public InvalidFormatException(String message) {
        super(message);
    }
    
    public InvalidFormatException(String field, String value) {
        super("Invalid format for " + field + ": " + value);
    }
    
    public InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public void handleResponse(Context ctx) {
        ctx.status(getStatusCode()).json(ResponseBuilder.error("Invalid format: " + getMessage()));
    }
    
    @Override
    public int getStatusCode() {
        return 400;
    }
}
