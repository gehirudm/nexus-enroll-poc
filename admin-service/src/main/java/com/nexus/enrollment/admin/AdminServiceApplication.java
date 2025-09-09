package com.nexus.enrollment.admin;

import com.nexus.enrollment.admin.service.AdminService;
import com.nexus.enrollment.admin.service.ReportService;
import com.nexus.enrollment.admin.handler.AdminHandler;
import io.javalin.Javalin;

public class AdminServiceApplication {
    
    private static final int PORT = 8084;
    
    public static void main(String[] args) {
        // Initialize services
        AdminService adminService = new AdminService();
        ReportService reportService = new ReportService();
        
        // Initialize handler
        AdminHandler adminHandler = new AdminHandler(adminService, reportService);
        
        // Create and configure Javalin app
        Javalin app = Javalin.create().start(PORT);
        
        // Register routes
        registerRoutes(app, adminHandler);
        
        System.out.println("Admin Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /admin/students - Get all students");
        System.out.println("  GET /admin/faculty - Get all faculty");
        System.out.println("  GET /admin/courses - Get all courses");
        System.out.println("  POST /admin/courses - Create new course");
        System.out.println("  PUT /admin/courses/{courseId} - Update course");
        System.out.println("  DELETE /admin/courses/{courseId} - Delete course");
        System.out.println("  POST /admin/students/{studentId}/force-enroll/{courseId} - Force enroll student");
        System.out.println("  GET /admin/reports/enrollment - Enrollment report");
        System.out.println("  GET /admin/reports/faculty-workload - Faculty workload report");
        System.out.println("  GET /admin/reports/course-trends - Course popularity trends");
    }
    
    private static void registerRoutes(Javalin app, AdminHandler adminHandler) {
        // Student endpoints
        app.get("/admin/students", adminHandler::getAllStudents);
        
        // Faculty endpoints
        app.get("/admin/faculty", adminHandler::getAllFaculty);
        
        // Course endpoints
        app.get("/admin/courses", adminHandler::getAllCourses);
        app.post("/admin/courses", adminHandler::createCourse);
        app.put("/admin/courses/{courseId}", adminHandler::updateCourse);
        app.delete("/admin/courses/{courseId}", adminHandler::deleteCourse);
        
        // Enrollment endpoints
        app.post("/admin/students/{studentId}/force-enroll/{courseId}", adminHandler::forceEnrollStudent);
        
        // Report endpoints
        app.get("/admin/reports/enrollment", adminHandler::generateEnrollmentReport);
        app.get("/admin/reports/faculty-workload", adminHandler::generateFacultyWorkloadReport);
        app.get("/admin/reports/course-trends", adminHandler::generateCourseTrendsReport);
    }
}
