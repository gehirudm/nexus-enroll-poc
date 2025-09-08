package com.nexus.enrollment.course.service;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;

/**
 * Service registry for Course Service operations
 * Provides convenient methods for other microservices to interact with Course Service
 */
public class CourseServiceRegistry {
    private static final ServiceClient serviceClient = new ServiceClient();
    
    /**
     * Get course by ID
     */
    public static ServiceResponse<Course> getCourse(Long courseId) {
        return serviceClient.get("course", "/courses/" + courseId, Course.class);
    }
    
    /**
     * Get all courses
     */
    public static ServiceResponse<String> getAllCourses() {
        return serviceClient.get("course", "/courses", String.class);
    }
    
    /**
     * Get courses by department
     */
    public static ServiceResponse<String> getCoursesByDepartment(String department) {
        return serviceClient.get("course", "/courses/department/" + department, String.class);
    }
    
    /**
     * Get courses by instructor/faculty ID
     */
    public static ServiceResponse<String> getCoursesByInstructor(Long facultyId) {
        return serviceClient.get("course", "/courses/instructor/" + facultyId, String.class);
    }
    
    /**
     * Search courses by keyword
     */
    public static ServiceResponse<String> searchCourses(String keyword) {
        return serviceClient.get("course", "/courses/search?keyword=" + keyword, String.class);
    }
    
    /**
     * Get available courses (with available spots)
     */
    public static ServiceResponse<String> getAvailableCourses() {
        return serviceClient.get("course", "/courses/available", String.class);
    }
    
    /**
     * Get course prerequisites
     */
    public static ServiceResponse<String> getCoursePrerequisites(Long courseId) {
        return serviceClient.get("course", "/courses/" + courseId + "/prerequisites", String.class);
    }
    
    /**
     * Get course enrollments count
     */
    public static ServiceResponse<String> getCourseEnrollments(Long courseId) {
        return serviceClient.get("course", "/courses/" + courseId + "/enrollments", String.class);
    }
    
    /**
     * Create a new course
     */
    public static ServiceResponse<Course> createCourse(Course course) {
        return serviceClient.post("course", "/courses", course, Course.class);
    }
    
    /**
     * Check if course exists
     */
    public static ServiceResponse<Boolean> courseExists(Long courseId) {
        ServiceResponse<Course> response = getCourse(courseId);
        if (response.isSuccess()) {
            return ServiceResponse.success(true);
        } else if (response.getMessage().contains("not found") || response.getMessage().contains("404")) {
            return ServiceResponse.success(false);
        } else {
            return ServiceResponse.error(response.getMessage());
        }
    }
}