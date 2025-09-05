package com.nexus.enrollment.student.validator;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.ValidationResult;

public interface EnrollmentValidator {
    ValidationResult validate(Student student, Long courseId);
}
