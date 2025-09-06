package com.nexus.enrollment.common.exceptions;

import io.javalin.http.Context;
import com.nexus.enrollment.common.util.ResponseBuilder;

public class SystemException extends HttpException {
    public SystemException(String message) {
        super(message);
    }
    
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public void handleResponse(Context ctx) {
        ctx.status(getStatusCode()).json(ResponseBuilder.error("System error: " + getMessage()));
    }
    
    @Override
    public int getStatusCode() {
        return 500;
    }
}
