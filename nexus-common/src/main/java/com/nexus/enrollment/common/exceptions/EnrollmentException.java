package com.nexus.enrollment.common.exceptions;

import io.javalin.http.Context;
import com.nexus.enrollment.common.util.ResponseBuilder;

public class EnrollmentException extends HttpException {
    public EnrollmentException(String message) {
        super(message);
    }
    
    public EnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public void handleResponse(Context ctx) {
        ctx.status(getStatusCode()).json(ResponseBuilder.error("Enrollment error: " + getMessage()));
    }
    
    @Override
    public int getStatusCode() {
        return 409; // Conflict - appropriate for enrollment conflicts
    }
}
