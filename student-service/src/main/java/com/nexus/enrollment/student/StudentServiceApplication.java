package com.nexus.enrollment.student;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.enums.EnrollmentStatus;
import com.nexus.enrollment.common.web.WebServer;
import com.nexus.enrollment.student.repository.StudentRepository;
import com.nexus.enrollment.student.repository.InMemoryStudentRepository;
import com.nexus.enrollment.student.service.StudentService;
import com.nexus.enrollment.student.service.EnrollmentService;
import com.nexus.enrollment.student.validator.EnrollmentValidator;
import com.nexus.enrollment.student.validator.PrerequisiteValidator;
import com.nexus.enrollment.student.handler.StudentHandler;
import io.javalin.Javalin;
import java.util.Arrays;
import java.util.List;

public class StudentServiceApplication {
    
    private static final int PORT = 8081;
    private static StudentHandler studentHandler;
    
    public static void main(String[] args) {
        // Initialize repositories
        StudentRepository studentRepo = new InMemoryStudentRepository();
        
        // Initialize validators
        List<EnrollmentValidator> validators = Arrays.asList(new PrerequisiteValidator());
        
        // Initialize services
        StudentService studentService = new StudentService(studentRepo);
        EnrollmentService enrollmentService = new EnrollmentService(studentRepo, validators);
        
        // Initialize handler
        studentHandler = new StudentHandler(studentService, enrollmentService);
        
        // Initialize with sample data
        initializeSampleData(studentRepo);
        
        // Start Javalin server
        startJavalinServer();
    }
    
    private static void startJavalinServer() {
        Javalin app = WebServer.createAndConfigureServer();
        
        app.start(PORT);
        
        System.out.println("Student Service started on port " + PORT);
        System.out.println("Available endpoints:");
        
        // Student Service Endpoints using StudentHandler methods
        app.get("/students", studentHandler::getAllStudents);
        app.post("/students", studentHandler::createStudent);
        app.get("/students/{id}", studentHandler::getStudentById);
        app.put("/students/{id}", studentHandler::updateStudent);
        app.delete("/students/{id}", studentHandler::deleteStudent);
        app.get("/students/{id}/schedule", studentHandler::getStudentSchedule);
        app.get("/students/{id}/enrollments", studentHandler::getStudentEnrollments);
        app.post("/students/{id}/enroll/{courseId}", studentHandler::enrollStudent);
        app.delete("/students/{id}/drop/{courseId}", studentHandler::dropCourse);
        app.get("/students/{id}/waitlisted", studentHandler::getWaitlistedCourses);
        app.post("/students/{id}/waitlist/{courseId}", studentHandler::addToWaitlist);
        
        System.out.println("  GET /students - Get all students");
        System.out.println("  POST /students - Create new student");
        System.out.println("  GET /students/{id} - Get student by ID");
        System.out.println("  PUT /students/{id} - Update student");
        System.out.println("  DELETE /students/{id} - Delete student");
        System.out.println("  GET /students/{id}/schedule - Get student's schedule");
        System.out.println("  GET /students/{id}/enrollments - Get student's enrollments");
        System.out.println("  POST /students/{id}/enroll/{courseId} - Enroll in course (or add to waitlist if full)");
        System.out.println("  DELETE /students/{id}/drop/{courseId} - Drop course");
        System.out.println("  GET /students/{id}/waitlisted - Get waitlisted courses");
        System.out.println("  POST /students/{id}/waitlist/{courseId} - Manually add to waitlist");
    }
    
    private static void initializeSampleData(StudentRepository repo) {
        Student student1 = new Student("John Doe", "john.doe@example.com", "Computer Science");
        Student student2 = new Student("Jane Smith", "jane.smith@example.com", "Mathematics");
        Student student3 = new Student("Bob Wilson", "bob.wilson@example.com", "Physics");
        
        // Add some sample enrollments to make the schedule more realistic
        Enrollment enrollment1 = new Enrollment(1L, 101L, EnrollmentStatus.ENROLLED);
        enrollment1.setId(1L);
        
        Enrollment enrollment2 = new Enrollment(1L, 102L, EnrollmentStatus.ENROLLED);
        enrollment2.setId(2L);
        
        Enrollment enrollment3 = new Enrollment(2L, 101L, EnrollmentStatus.ENROLLED);
        enrollment3.setId(3L);
        
        student1.getEnrollments().add(enrollment1);
        student1.getEnrollments().add(enrollment2);
        student2.getEnrollments().add(enrollment3);
        
        repo.save(student1);
        repo.save(student2);
        repo.save(student3);
        
        System.out.println("Sample data initialized - 3 students created with sample enrollments");
    }
}
