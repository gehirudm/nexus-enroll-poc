package com.nexus.enrollment.student.service;

import com.nexus.enrollment.common.model.EnrollmentResult;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.ValidationResult;
import com.nexus.enrollment.common.enums.EnrollmentStatus;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;
import com.nexus.enrollment.student.repository.StudentRepository;
import com.nexus.enrollment.student.validator.EnrollmentValidator;

import java.util.List;
import java.util.ArrayList;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final StudentRepository studentRepository;
    private final List<EnrollmentValidator> validators;
    private final ServiceClient serviceClient;
    
    public EnrollmentServiceImpl(StudentRepository studentRepository, List<EnrollmentValidator> validators) {
        this.studentRepository = studentRepository;
        this.validators = validators != null ? validators : new ArrayList<>();
        this.serviceClient = new ServiceClient();
    }
    
    @Override
    public EnrollmentResult enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // Check if course exists using Course Service
        ServiceResponse<Course> courseResponse = serviceClient.get("course", "/courses/" + courseId, Course.class);
        if (!courseResponse.isSuccess()) {
            return new EnrollmentResult(false, "Course not found: " + courseResponse.getMessage(), null);
        }
        
        // Validation logic using Strategy pattern
        for (EnrollmentValidator validator : validators) {
            ValidationResult result = validator.validate(student, courseId);
            if (!result.isValid()) {
                return new EnrollmentResult(false, result.getMessage(), null);
            }
        }
        
        // Check if already enrolled
        boolean alreadyEnrolled = student.getEnrollments().stream()
                .anyMatch(e -> e.getCourseId().equals(courseId) && e.getStatus() == EnrollmentStatus.ENROLLED);
        
        if (alreadyEnrolled) {
            return new EnrollmentResult(false, "Student is already enrolled in this course", null);
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.ENROLLED);
        student.getEnrollments().add(enrollment);
        studentRepository.save(student);
        
        // Send enrollment confirmation notification
        serviceClient.post("notification", "/notifications", 
            new NotificationRequest(studentId, courseId, "ENROLLMENT_CONFIRMATION"), String.class);
        
        return new EnrollmentResult(true, "Enrollment successful", enrollment);
    }
    
    @Override
    public EnrollmentResult dropCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // Find the enrollment
        Enrollment enrollment = student.getEnrollments().stream()
                .filter(e -> e.getCourseId().equals(courseId) && e.getStatus() == EnrollmentStatus.ENROLLED)
                .findFirst()
                .orElse(null);
        
        if (enrollment == null) {
            return new EnrollmentResult(false, "Student is not enrolled in this course", null);
        }
        
        // Update enrollment status
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        studentRepository.save(student);
        
        // Send drop confirmation notification
        serviceClient.post("notification", "/notifications", 
            new NotificationRequest(studentId, courseId, "DROP_CONFIRMATION"), String.class);
        
        return new EnrollmentResult(true, "Course dropped successfully", enrollment);
    }
    
    @Override
    public List<Course> getWaitlistedCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // Get waitlisted enrollments for this student
        List<Long> waitlistedCourseIds = student.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.WAITLISTED)
                .map(Enrollment::getCourseId)
                .toList();
        
        // Fetch course details from Course Service for each waitlisted course
        List<Course> waitlistedCourses = new ArrayList<>();
        for (Long courseId : waitlistedCourseIds) {
            ServiceResponse<Course> courseResponse = serviceClient.get("course", "/courses/" + courseId, Course.class);
            if (courseResponse.isSuccess()) {
                waitlistedCourses.add(courseResponse.getData());
            }
        }
        
        return waitlistedCourses;
    }
    
    // Helper class for notification requests
    private static class NotificationRequest {
        private final Long studentId;
        private final Long courseId;
        private final String notificationType;
        
        public NotificationRequest(Long studentId, Long courseId, String notificationType) {
            this.studentId = studentId;
            this.courseId = courseId;
            this.notificationType = notificationType;
        }
        
        public Long getStudentId() { return studentId; }
        public Long getCourseId() { return courseId; }
        public String getNotificationType() { return notificationType; }
    }
}
