package com.nexus.enrollment.student.service;

import com.nexus.enrollment.common.model.EnrollmentResult;
import com.nexus.enrollment.common.model.Course;
import java.util.List;

public interface EnrollmentService {
    EnrollmentResult enrollStudent(Long studentId, Long courseId);
    EnrollmentResult dropCourse(Long studentId, Long courseId);
    List<Course> getWaitlistedCourses(Long studentId);
}
