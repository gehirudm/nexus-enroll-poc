package com.nexus.enrollment.common.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found");
    }
    
    public NotFoundException(String message) {
        super(message);
    }
}
