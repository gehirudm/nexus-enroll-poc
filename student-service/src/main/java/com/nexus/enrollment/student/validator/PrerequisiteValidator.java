package com.nexus.enrollment.student.validator;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.ValidationResult;

public class PrerequisiteValidator implements EnrollmentValidator {
    
    @Override
    public ValidationResult validate(Student student, Long courseId) {
        // For now, return a simple validation
        // In a real implementation, this would check course prerequisites
        return new ValidationResult(true, "Prerequisites satisfied");
    }
}
