package com.nexus.enrollment.common.exceptions;

import io.javalin.http.Context;
import com.nexus.enrollment.common.util.ResponseBuilder;

public class NotFoundException extends HttpException {
    public NotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found");
    }
    
    public NotFoundException(String message) {
        super(message);
    }
    
    @Override
    public void handleResponse(Context ctx) {
        ctx.status(getStatusCode()).json(ResponseBuilder.error(getMessage()));
    }
    
    @Override
    public int getStatusCode() {
        return 404;
    }
}
