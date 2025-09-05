package student_service.service;

import common.model.EnrollmentResult;
import common.model.Course;
import java.util.List;

public interface EnrollmentService {
    EnrollmentResult enrollStudent(Long studentId, Long courseId);
    EnrollmentResult dropCourse(Long studentId, Long courseId);
    List<Course> getWaitlistedCourses(Long studentId);
}
