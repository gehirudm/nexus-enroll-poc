package com.nexus.enrollment.common.exceptions;

import io.javalin.http.Context;
import com.nexus.enrollment.common.util.ResponseBuilder;

public class BadRequestException extends HttpException {
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public void handleResponse(Context ctx) {
        ctx.status(getStatusCode()).json(ResponseBuilder.error(getMessage()));
    }
    
    @Override
    public int getStatusCode() {
        return 400;
    }
}
