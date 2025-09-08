package com.nexus.enrollment.admin.service;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;

/**
 * Service registry for Admin Service operations
 * Provides convenient methods for other microservices to interact with Admin Service
 */
public class AdminServiceRegistry {
    private static final ServiceClient serviceClient = new ServiceClient();
    
    /**
     * Get enrollment report
     */
    public static ServiceResponse<String> getEnrollmentReport(String department, String semester) {
        String endpoint = "/admin/reports/enrollment";
        if (department != null || semester != null) {
            endpoint += "?";
            if (department != null) endpoint += "department=" + department;
            if (semester != null) {
                if (department != null) endpoint += "&";
                endpoint += "semester=" + semester;
            }
        }
        return serviceClient.get("admin", endpoint, String.class);
    }
    
    /**
     * Get faculty workload report
     */
    public static ServiceResponse<String> getFacultyWorkloadReport() {
        return serviceClient.get("admin", "/admin/reports/faculty-workload", String.class);
    }
    
    /**
     * Get course popularity trends report
     */
    public static ServiceResponse<String> getCourseTrendsReport() {
        return serviceClient.get("admin", "/admin/reports/course-trends", String.class);
    }
    
    /**
     * Create new course (admin privilege)
     */
    public static ServiceResponse<Course> createCourse(Course course) {
        return serviceClient.post("admin", "/admin/courses", course, Course.class);
    }
    
    /**
     * Update course (admin privilege)
     */
    public static ServiceResponse<Course> updateCourse(Long courseId, Course course) {
        return serviceClient.put("admin", "/admin/courses/" + courseId, course, Course.class);
    }
    
    /**
     * Delete course (admin privilege)
     */
    public static ServiceResponse<String> deleteCourse(Long courseId) {
        return serviceClient.delete("admin", "/admin/courses/" + courseId, String.class);
    }
    
    /**
     * Force enroll student (admin privilege)
     */
    public static ServiceResponse<String> forceEnrollStudent(Long studentId, Long courseId) {
        return serviceClient.post("admin", "/admin/students/" + studentId + "/force-enroll/" + courseId, null, String.class);
    }
    
    /**
     * Get all students (admin view)
     */
    public static ServiceResponse<String> getAllStudents() {
        return serviceClient.get("admin", "/admin/students", String.class);
    }
    
    /**
     * Get all faculty (admin view)
     */
    public static ServiceResponse<String> getAllFaculty() {
        return serviceClient.get("admin", "/admin/faculty", String.class);
    }
}
