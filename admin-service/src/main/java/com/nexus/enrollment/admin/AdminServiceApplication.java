package com.nexus.enrollment.admin;

import com.nexus.enrollment.admin.service.AdminService;
import com.nexus.enrollment.admin.service.AdminServiceImpl;
import com.nexus.enrollment.admin.service.ReportService;
import com.nexus.enrollment.admin.service.ReportServiceImpl;
import com.nexus.enrollment.admin.controller.AdminController;
import com.nexus.enrollment.admin.handler.AdminReportsHandler;
import com.nexus.enrollment.admin.handler.AdminCoursesHandler;
import com.nexus.enrollment.admin.handler.AdminStudentsHandler;
import com.nexus.enrollment.admin.handler.AdminFacultyHandler;
import com.nexus.enrollment.admin.handler.AdminHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class AdminServiceApplication {
    
    private static final int PORT = 8084;
    private static AdminController controller;
    
    public static void main(String[] args) throws IOException {
        // Initialize services
        AdminService adminService = new AdminServiceImpl();
        ReportService reportService = new ReportServiceImpl();
        
        // Initialize controller
        controller = new AdminController(adminService, reportService);
        
        // Start HTTP server
        startHttpServer();
    }
    
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Admin Service Endpoints
        server.createContext("/admin/reports", new AdminReportsHandler(controller));
        server.createContext("/admin/courses", new AdminCoursesHandler(controller));
        server.createContext("/admin/students", new AdminStudentsHandler(controller));
        server.createContext("/admin/faculty", new AdminFacultyHandler(controller));
        server.createContext("/admin/", new AdminHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Admin Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /admin/reports/enrollment?department={}&semester={} - Enrollment report");
        System.out.println("  GET /admin/reports/faculty-workload - Faculty workload report");
        System.out.println("  GET /admin/reports/course-trends - Course popularity trends");
        System.out.println("  POST /admin/courses - Create new course");
        System.out.println("  PUT /admin/courses/{id} - Update course");
        System.out.println("  DELETE /admin/courses/{id} - Delete course");
        System.out.println("  POST /admin/students/{id}/force-enroll/{courseId} - Force enroll student");
        System.out.println("  GET /admin/students - Get all students");
        System.out.println("  GET /admin/faculty - Get all faculty");
    }
}
