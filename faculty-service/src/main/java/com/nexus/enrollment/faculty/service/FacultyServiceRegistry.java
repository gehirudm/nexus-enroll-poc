package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;

/**
 * Service registry for Faculty Service operations
 * Provides convenient methods for other microservices to interact with Faculty Service
 */
public class FacultyServiceRegistry {
    private static final ServiceClient serviceClient = new ServiceClient();
    
    /**
     * Get faculty by ID
     */
    public static ServiceResponse<Faculty> getFaculty(Long facultyId) {
        return serviceClient.get("faculty", "/faculty/" + facultyId, Faculty.class);
    }
    
    /**
     * Get faculty's assigned courses
     */
    public static ServiceResponse<String> getFacultyCourses(Long facultyId) {
        return serviceClient.get("faculty", "/faculty/" + facultyId + "/courses", String.class);
    }
    
    /**
     * Get class roster for a course
     */
    public static ServiceResponse<String> getClassRoster(Long facultyId, Long courseId) {
        return serviceClient.get("faculty", "/faculty/" + facultyId + "/roster/" + courseId, String.class);
    }
    
    /**
     * Submit grades for a course
     */
    public static ServiceResponse<String> submitGrades(Long facultyId, Object grades) {
        return serviceClient.post("faculty", "/faculty/" + facultyId + "/grades", grades, String.class);
    }
    
    /**
     * Get submitted grades for a course
     */
    public static ServiceResponse<String> getSubmittedGrades(Long facultyId, Long courseId) {
        return serviceClient.get("faculty", "/faculty/" + facultyId + "/grades/" + courseId, String.class);
    }
    
    /**
     * Submit course change request
     */
    public static ServiceResponse<String> submitCourseRequest(Long facultyId, Object request) {
        return serviceClient.put("faculty", "/faculty/" + facultyId + "/course-request", request, String.class);
    }
    
    /**
     * Assign course to faculty
     */
    public static ServiceResponse<String> assignCourse(Long facultyId, Long courseId) {
        return serviceClient.post("faculty", "/faculty/" + facultyId + "/courses/" + courseId, null, String.class);
    }
    
    /**
     * Get pending grades for approval
     */
    public static ServiceResponse<String> getPendingGrades(Long facultyId) {
        return serviceClient.get("faculty", "/faculty/" + facultyId + "/grades/pending", String.class);
    }
    
    /**
     * Get pending grades for a specific course
     */
    public static ServiceResponse<String> getPendingGradesForCourse(Long facultyId, Long courseId) {
        return serviceClient.get("faculty", "/faculty/" + facultyId + "/grades/pending?courseId=" + courseId, String.class);
    }
    
    /**
     * Approve a pending grade
     */
    public static ServiceResponse<String> approveGrade(Long facultyId, Long gradeId) {
        return serviceClient.post("faculty", "/faculty/" + facultyId + "/grades/" + gradeId + "/approve", null, String.class);
    }
    
    /**
     * Reject a pending grade with reason
     */
    public static ServiceResponse<String> rejectGrade(Long facultyId, Long gradeId, String reason) {
        return serviceClient.post("faculty", "/faculty/" + facultyId + "/grades/" + gradeId + "/reject", reason, String.class);
    }
    
    /**
     * Check if faculty exists
     */
    public static ServiceResponse<Boolean> facultyExists(Long facultyId) {
        ServiceResponse<Faculty> response = getFaculty(facultyId);
        if (response.isSuccess()) {
            return ServiceResponse.success(true);
        } else if (response.getMessage().contains("not found") || response.getMessage().contains("404")) {
            return ServiceResponse.success(false);
        } else {
            return ServiceResponse.error(response.getMessage());
        }
    }
}
