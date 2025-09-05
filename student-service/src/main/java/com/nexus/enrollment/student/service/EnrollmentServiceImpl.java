package com.nexus.enrollment.student.service;

import com.nexus.enrollment.common.model.EnrollmentResult;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.ValidationResult;
import com.nexus.enrollment.common.enums.EnrollmentStatus;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.student.repository.StudentRepository;
import com.nexus.enrollment.student.validator.EnrollmentValidator;

import java.util.List;
import java.util.ArrayList;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final StudentRepository studentRepository;
    private final List<EnrollmentValidator> validators;
    
    public EnrollmentServiceImpl(StudentRepository studentRepository, List<EnrollmentValidator> validators) {
        this.studentRepository = studentRepository;
        this.validators = validators != null ? validators : new ArrayList<>();
    }
    
    @Override
    public EnrollmentResult enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
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
        
        return new EnrollmentResult(true, "Course dropped successfully", enrollment);
    }
    
    @Override
    public List<Course> getWaitlistedCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // For now, return empty list since we don't have course service integration yet
        // In real implementation, this would fetch course details for waitlisted enrollments
        return new ArrayList<>();
    }
}
