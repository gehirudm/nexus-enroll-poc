package com.nexus.enrollment.admin.handler;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.admin.service.AdminService;
import com.nexus.enrollment.admin.service.ReportService;
import com.nexus.enrollment.admin.service.EnrollmentReport;
import com.nexus.enrollment.admin.service.FacultyWorkloadReport;
import com.nexus.enrollment.admin.service.CourseTrendsReport;
import io.javalin.http.Context;
import java.util.List;

public class AdminHandler {
    private final AdminService adminService;
    private final ReportService reportService;
    
    public AdminHandler(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }
    
    /**
     * GET /admin/students - Get all students
     */
    public void getAllStudents(Context ctx) {
        try {
            List<Student> students = adminService.getAllStudents();
            ctx.json(createSuccessResponse("Students retrieved successfully", students));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /admin/faculty - Get all faculty
     */
    public void getAllFaculty(Context ctx) {
        try {
            List<Faculty> faculty = adminService.getAllFaculty();
            ctx.json(createSuccessResponse("Faculty retrieved successfully", faculty));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /admin/courses - Get all courses
     */
    public void getAllCourses(Context ctx) {
        try {
            List<Course> courses = adminService.getAllCourses();
            ctx.json(createSuccessResponse("Courses retrieved successfully", courses));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /admin/courses - Create a new course
     */
    public void createCourse(Context ctx) {
        try {
            Course course = ctx.bodyAsClass(Course.class);
            Course createdCourse = adminService.createCourse(course);
            ctx.status(201).json(createSuccessResponse("Course created successfully", createdCourse));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * PUT /admin/courses/{courseId} - Update a course
     */
    public void updateCourse(Context ctx) {
        try {
            Long courseId = Long.valueOf(ctx.pathParam("courseId"));
            Course course = ctx.bodyAsClass(Course.class);
            Course updatedCourse = adminService.updateCourse(courseId, course);
            ctx.json(createSuccessResponse("Course updated successfully", updatedCourse));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * DELETE /admin/courses/{courseId} - Delete a course
     */
    public void deleteCourse(Context ctx) {
        try {
            Long courseId = Long.valueOf(ctx.pathParam("courseId"));
            adminService.deleteCourse(courseId);
            ctx.json(createSuccessResponse("Course deleted successfully", null));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /admin/students/{studentId}/force-enroll/{courseId} - Force enroll student
     */
    public void forceEnrollStudent(Context ctx) {
        try {
            Long studentId = Long.valueOf(ctx.pathParam("studentId"));
            Long courseId = Long.valueOf(ctx.pathParam("courseId"));
            adminService.forceEnrollStudent(studentId, courseId);
            ctx.json(createSuccessResponse("Student force enrolled successfully", null));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /admin/reports/enrollment - Generate enrollment report
     */
    public void generateEnrollmentReport(Context ctx) {
        try {
            String department = ctx.queryParam("department");
            String semester = ctx.queryParam("semester");
            EnrollmentReport report = reportService.generateEnrollmentReport(department, semester);
            ctx.json(createSuccessResponse("Enrollment report generated successfully", report));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /admin/reports/faculty-workload - Generate faculty workload report
     */
    public void generateFacultyWorkloadReport(Context ctx) {
        try {
            FacultyWorkloadReport report = reportService.generateFacultyWorkloadReport();
            ctx.json(createSuccessResponse("Faculty workload report generated successfully", report));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /admin/reports/course-trends - Generate course trends report
     */
    public void generateCourseTrendsReport(Context ctx) {
        try {
            CourseTrendsReport report = reportService.generateCourseTrendsReport();
            ctx.json(createSuccessResponse("Course trends report generated successfully", report));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    // Helper methods for response formatting
    private Object createSuccessResponse(String message, Object data) {
        return new ResponseWrapper("success", message, data);
    }
    
    private Object createErrorResponse(String message) {
        return new ResponseWrapper("error", message, null);
    }
    
    // Response wrapper class
    public static class ResponseWrapper {
        public final String status;
        public final String message;
        public final Object data;
        
        public ResponseWrapper(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }
}
