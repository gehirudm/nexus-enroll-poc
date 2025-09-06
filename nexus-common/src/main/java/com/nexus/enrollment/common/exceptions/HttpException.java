package com.nexus.enrollment.common.exceptions;

import io.javalin.http.Context;

public abstract class HttpException extends RuntimeException {
    
    public HttpException(String message) {
        super(message);
    }
    
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Handle the HTTP response for this exception
     * @param ctx Javalin context to set status and response body
     */
    public abstract void handleResponse(Context ctx);
    
    /**
     * Get the HTTP status code for this exception
     * @return HTTP status code
     */
    public abstract int getStatusCode();
}
