package com.nexus.enrollment.common.registries;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;

/**
 * Service registry for Student Service operations
 * Provides convenient methods for other microservices to interact with Student Service
 */
public class StudentServiceRegistry {
    private static final ServiceClient serviceClient = new ServiceClient();
    
    /**
     * Get student by ID
     */
    public static ServiceResponse<Student> getStudent(Long studentId) {
        return serviceClient.get("student", "/students/" + studentId, Student.class);
    }
    
    /**
     * Get all students
     */
    public static ServiceResponse<String> getAllStudents() {
        return serviceClient.get("student", "/students", String.class);
    }
    
    /**
     * Create a new student
     */
    public static ServiceResponse<Student> createStudent(Student student) {
        return serviceClient.post("student", "/students", student, Student.class);
    }
    
    /**
     * Get student's schedule
     */
    public static ServiceResponse<String> getStudentSchedule(Long studentId) {
        return serviceClient.get("student", "/students/" + studentId + "/schedule", String.class);
    }
    
    /**
     * Get student enrollments
     */
    public static ServiceResponse<String> getStudentEnrollments(Long studentId) {
        return serviceClient.get("student", "/students/" + studentId + "/enrollments", String.class);
    }
    
    /**
     * Enroll student in a course
     */
    public static ServiceResponse<String> enrollStudent(Long studentId, Long courseId) {
        return serviceClient.post("student", "/students/" + studentId + "/enroll/" + courseId, null, String.class);
    }
    
    /**
     * Drop student from a course
     */
    public static ServiceResponse<String> dropCourse(Long studentId, Long courseId) {
        return serviceClient.delete("student", "/students/" + studentId + "/drop/" + courseId, String.class);
    }
    
    /**
     * Check if student exists
     */
    public static ServiceResponse<Boolean> studentExists(Long studentId) {
        ServiceResponse<Student> response = getStudent(studentId);
        if (response.isSuccess()) {
            return ServiceResponse.success(true);
        } else if (response.getMessage().contains("not found") || response.getMessage().contains("404")) {
            return ServiceResponse.success(false);
        } else {
            return ServiceResponse.error(response.getMessage());
        }
    }
}
