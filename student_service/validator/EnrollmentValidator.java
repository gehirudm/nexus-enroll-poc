package student_service.validator;

import common.model.Student;
import common.model.ValidationResult;

public interface EnrollmentValidator {
    ValidationResult validate(Student student, Long courseId);
}
