package admin_service;

import admin_service.service.AdminService;
import admin_service.service.AdminServiceImpl;
import admin_service.service.ReportService;
import admin_service.service.ReportServiceImpl;
import admin_service.controller.AdminController;

public class AdminServiceApplication {
    
    public static void main(String[] args) {
        // Initialize services
        AdminService adminService = new AdminServiceImpl();
        ReportService reportService = new ReportServiceImpl();
        
        // Initialize controller
        AdminController controller = new AdminController(adminService, reportService);
        
        System.out.println("Admin Service Application started successfully!");
        
        // Demo usage
        demonstrateService(controller);
    }
    
    private static void demonstrateService(AdminController controller) {
        System.out.println("\n=== Admin Service Demo ===");
        
        // Generate enrollment report
        var enrollmentReport = controller.generateEnrollmentReport("Computer Science", "Fall 2024");
        System.out.println("Enrollment report: " + enrollmentReport.isSuccess());
        
        // Generate faculty workload report
        var workloadReport = controller.generateFacultyWorkloadReport();
        System.out.println("Faculty workload report: " + workloadReport.isSuccess());
        
        // Generate course trends report
        var trendsReport = controller.generateCourseTrendsReport();
        System.out.println("Course trends report: " + trendsReport.isSuccess());
        
        // Get all students
        var studentsResponse = controller.getAllStudents();
        System.out.println("Get all students: " + studentsResponse.isSuccess());
        
        System.out.println("Demo completed!");
    }
}
