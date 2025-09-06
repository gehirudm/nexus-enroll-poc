package com.nexus.enrollment.student;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.enums.EnrollmentStatus;
import com.nexus.enrollment.student.repository.StudentRepository;
import com.nexus.enrollment.student.repository.InMemoryStudentRepository;
import com.nexus.enrollment.student.service.StudentService;
import com.nexus.enrollment.student.service.StudentServiceImpl;
import com.nexus.enrollment.student.service.EnrollmentService;
import com.nexus.enrollment.student.service.EnrollmentServiceImpl;
import com.nexus.enrollment.student.validator.EnrollmentValidator;
import com.nexus.enrollment.student.validator.PrerequisiteValidator;
import com.nexus.enrollment.student.controller.StudentController;
import com.nexus.enrollment.student.handler.StudentsHandler;
import com.nexus.enrollment.student.handler.StudentByIdHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class StudentServiceApplication {
    
    private static final int PORT = 8081;
    private static StudentController controller;
    
    public static void main(String[] args) throws IOException {
        // Initialize repositories
        StudentRepository studentRepo = new InMemoryStudentRepository();
        
        // Initialize validators
        List<EnrollmentValidator> validators = Arrays.asList(new PrerequisiteValidator());
        
        // Initialize services
        StudentService studentService = new StudentServiceImpl(studentRepo);
        EnrollmentService enrollmentService = new EnrollmentServiceImpl(studentRepo, validators);
        
        // Initialize controller
        controller = new StudentController(studentService, enrollmentService);
        
        // Initialize with sample data
        initializeSampleData(studentRepo);
        
        // Start HTTP server
        startHttpServer();
    }
    
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Student Service Endpoints
        server.createContext("/students", new StudentsHandler(controller));
        server.createContext("/students/", new StudentByIdHandler(controller));
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Student Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /students - Get all students");
        System.out.println("  POST /students - Create new student");
        System.out.println("  GET /students/{id} - Get student by ID");
        System.out.println("  GET /students/{id}/schedule - Get student's schedule");
        System.out.println("  GET /students/{id}/enrollments - Get student's enrollments");
        System.out.println("  POST /students/{id}/enroll/{courseId} - Enroll in course");
        System.out.println("  DELETE /students/{id}/drop/{courseId} - Drop course");
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
