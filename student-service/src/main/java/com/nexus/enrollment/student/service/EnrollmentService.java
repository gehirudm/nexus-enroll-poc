package com.nexus.enrollment.student.service;

import com.nexus.enrollment.common.model.EnrollmentResult;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.ValidationResult;
import com.nexus.enrollment.common.enums.EnrollmentStatus;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.common.service.ServiceResponse;
import com.nexus.enrollment.common.registries.CourseServiceRegistry;
import com.nexus.enrollment.common.registries.NotificationServiceRegistry;
import com.nexus.enrollment.common.registries.AdminServiceRegistry;
import com.nexus.enrollment.student.repository.StudentRepository;
import com.nexus.enrollment.student.validator.EnrollmentValidator;

import java.util.List;
import java.util.ArrayList;

public class EnrollmentService {
    private final StudentRepository studentRepository;
    private final List<EnrollmentValidator> validators;
    
    public EnrollmentService(StudentRepository studentRepository, List<EnrollmentValidator> validators) {
        this.studentRepository = studentRepository;
        this.validators = validators != null ? validators : new ArrayList<>();
    }
    
    public EnrollmentResult enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // Check if course exists using Course Service
        ServiceResponse<Course> courseResponse = CourseServiceRegistry.getCourse(courseId);
        if (!courseResponse.isSuccess()) {
            return new EnrollmentResult(false, "Course not found: " + courseResponse.getMessage(), null);
        }
        
        Course course = courseResponse.getData();
        
        // Validation logic using Strategy pattern
        for (EnrollmentValidator validator : validators) {
            ValidationResult result = validator.validate(student, courseId);
            if (!result.isValid()) {
                return new EnrollmentResult(false, result.getMessage(), null);
            }
        }
        
        // Check if already enrolled or waitlisted
        boolean alreadyEnrolled = student.getEnrollments().stream()
                .anyMatch(e -> e.getCourseId().equals(courseId) && 
                         (e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.WAITLISTED));
        
        if (alreadyEnrolled) {
            return new EnrollmentResult(false, "Student is already enrolled or waitlisted for this course", null);
        }
        
        // Check if course is full - if so, add to waitlist
        Enrollment enrollment;
        if (course.isFull()) {
            // Add to waitlist
            enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.WAITLISTED);
            student.getEnrollments().add(enrollment);
            studentRepository.save(student);
            
            // Send waitlist notification
            NotificationServiceRegistry.sendWaitlistNotification(studentId, courseId);
            
            return new EnrollmentResult(true, "Course is full. You have been added to the waitlist.", enrollment);
        } else {
            // Enroll directly
            enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.ENROLLED);
            student.getEnrollments().add(enrollment);
            studentRepository.save(student);
            
            // Update course capacity
            course.decrementSeats();
            AdminServiceRegistry.updateCourse(courseId, course);
            
            // Send enrollment confirmation notification
            NotificationServiceRegistry.sendEnrollmentConfirmation(studentId, courseId);
            
            return new EnrollmentResult(true, "Enrollment successful", enrollment);
        }
    }
    
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
        
        // Get course information to update capacity
        ServiceResponse<Course> courseResponse = CourseServiceRegistry.getCourse(courseId);
        if (courseResponse.isSuccess()) {
            Course course = courseResponse.getData();
            course.incrementSeats();
            
            // Update course in Course Service using AdminServiceRegistry
            AdminServiceRegistry.updateCourse(courseId, course);
            
            // Process waitlist - find the next waitlisted student
            processWaitlist(courseId);
        }
        
        // Send drop confirmation notification
        NotificationServiceRegistry.sendDropConfirmation(studentId, courseId);
        
        return new EnrollmentResult(true, "Course dropped successfully", enrollment);
    }
    
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
            ServiceResponse<Course> courseResponse = CourseServiceRegistry.getCourse(courseId);
            if (courseResponse.isSuccess()) {
                waitlistedCourses.add(courseResponse.getData());
            }
        }
        
        return waitlistedCourses;
    }
    
    /**
     * Manually add student to waitlist for a course (even if course is not full)
     */
    public EnrollmentResult addToWaitlist(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // Check if course exists
        ServiceResponse<Course> courseResponse = CourseServiceRegistry.getCourse(courseId);
        if (!courseResponse.isSuccess()) {
            return new EnrollmentResult(false, "Course not found: " + courseResponse.getMessage(), null);
        }
        
        // Check if already enrolled or waitlisted
        boolean alreadyRelated = student.getEnrollments().stream()
                .anyMatch(e -> e.getCourseId().equals(courseId) && 
                         (e.getStatus() == EnrollmentStatus.ENROLLED || 
                          e.getStatus() == EnrollmentStatus.WAITLISTED));
        
        if (alreadyRelated) {
            return new EnrollmentResult(false, "Student is already enrolled or waitlisted for this course", null);
        }
        
        // Add to waitlist
        Enrollment enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.WAITLISTED);
        student.getEnrollments().add(enrollment);
        studentRepository.save(student);
        
        // Send waitlist notification
        NotificationServiceRegistry.sendWaitlistNotification(studentId, courseId);
        
        return new EnrollmentResult(true, "Successfully added to waitlist", enrollment);
    }
    
    /**
     * Process waitlist when a spot becomes available in a course
     */
    private void processWaitlist(Long courseId) {
        // Find all students waitlisted for this course across all students
        List<Student> allStudents = studentRepository.findAll();
        
        // Find the first waitlisted student for this course (FIFO)
        Student nextStudent = null;
        Enrollment waitlistEnrollment = null;
        
        for (Student student : allStudents) {
            for (Enrollment enrollment : student.getEnrollments()) {
                if (enrollment.getCourseId().equals(courseId) && 
                    enrollment.getStatus() == EnrollmentStatus.WAITLISTED) {
                    nextStudent = student;
                    waitlistEnrollment = enrollment;
                    break;
                }
            }
            if (nextStudent != null) break;
        }
        
        // If we found a waitlisted student, enroll them
        if (nextStudent != null && waitlistEnrollment != null) {
            // Change status from waitlisted to enrolled
            waitlistEnrollment.setStatus(EnrollmentStatus.ENROLLED);
            studentRepository.save(nextStudent);
            
            // Update course capacity
            ServiceResponse<Course> courseResponse = CourseServiceRegistry.getCourse(courseId);
            if (courseResponse.isSuccess()) {
                Course course = courseResponse.getData();
                course.decrementSeats();
                AdminServiceRegistry.updateCourse(courseId, course);
            }
            
            // Send notification to the student that they've been enrolled from waitlist
            NotificationServiceRegistry.sendEnrollmentConfirmation(nextStudent.getId(), courseId);
        }
    }
}
