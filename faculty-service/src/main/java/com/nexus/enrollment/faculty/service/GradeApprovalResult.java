package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Grade;

/**
 * Result object for grade approval operations
 */
public class GradeApprovalResult {
    private final boolean success;
    private final String message;
    private final Grade grade;
    
    public GradeApprovalResult(boolean success, String message, Grade grade) {
        this.success = success;
        this.message = message;
        this.grade = grade;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Grade getGrade() {
        return grade;
    }
    
    @Override
    public String toString() {
        return "GradeApprovalResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", grade=" + grade +
                '}';
    }
}
