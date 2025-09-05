import student_service.StudentServiceApplication;
import course_service.CourseServiceApplication;
import faculty_service.FacultyServiceApplication;
import admin_service.AdminServiceApplication;
import notification_service.NotificationServiceApplication;

public class NexusEnrollmentSystem {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("        NEXUS ENROLLMENT SYSTEM - POC");
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Start all microservices
        System.out.println("Starting all microservices...");
        System.out.println();
        
        try {
            // Student Service
            System.out.println("1. Starting Student Service...");
            StudentServiceApplication.main(new String[]{});
            System.out.println();
            
            // Course Service
            System.out.println("2. Starting Course Service...");
            CourseServiceApplication.main(new String[]{});
            System.out.println();
            
            // Faculty Service
            System.out.println("3. Starting Faculty Service...");
            FacultyServiceApplication.main(new String[]{});
            System.out.println();
            
            // Admin Service
            System.out.println("4. Starting Admin Service...");
            AdminServiceApplication.main(new String[]{});
            System.out.println();
            
            // Notification Service
            System.out.println("5. Starting Notification Service...");
            NotificationServiceApplication.main(new String[]{});
            System.out.println();
            
            System.out.println("=".repeat(60));
            System.out.println("   ALL MICROSERVICES STARTED SUCCESSFULLY!");
            System.out.println("=".repeat(60));
            System.out.println();
            
            printServiceEndpoints();
            
        } catch (Exception e) {
            System.err.println("Error starting services: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printServiceEndpoints() {
        System.out.println("AVAILABLE SERVICE ENDPOINTS:");
        System.out.println("-".repeat(40));
        
        System.out.println("STUDENT SERVICE:");
        System.out.println("  GET    /students/{id}");
        System.out.println("  GET    /students/{id}/schedule");
        System.out.println("  GET    /students/{id}/enrollments");
        System.out.println("  POST   /students/{id}/enroll/{courseId}");
        System.out.println("  DELETE /students/{id}/drop/{courseId}");
        System.out.println();
        
        System.out.println("COURSE SERVICE:");
        System.out.println("  GET    /courses");
        System.out.println("  GET    /courses/{id}");
        System.out.println("  GET    /courses/department/{dept}");
        System.out.println("  GET    /courses/available");
        System.out.println("  POST   /courses");
        System.out.println();
        
        System.out.println("FACULTY SERVICE:");
        System.out.println("  GET    /faculty/{id}");
        System.out.println("  GET    /faculty/{id}/courses");
        System.out.println("  GET    /faculty/{id}/roster/{courseId}");
        System.out.println("  POST   /faculty/{id}/grades");
        System.out.println();
        
        System.out.println("ADMIN SERVICE:");
        System.out.println("  GET    /admin/students");
        System.out.println("  GET    /admin/faculty");
        System.out.println("  GET    /admin/reports/enrollment");
        System.out.println("  GET    /admin/reports/faculty-workload");
        System.out.println("  POST   /admin/courses");
        System.out.println();
        
        System.out.println("NOTIFICATION SERVICE:");
        System.out.println("  POST   /notifications");
        System.out.println("  GET    /notifications/user/{userId}");
        System.out.println("  GET    /notifications/type/{type}");
        System.out.println("  PUT    /notifications/{id}/read");
        System.out.println();
        
        System.out.println("NOTE: This is a Proof of Concept implementation.");
        System.out.println("In a production environment, each service would run");
        System.out.println("on separate ports with REST API endpoints.");
    }
}
