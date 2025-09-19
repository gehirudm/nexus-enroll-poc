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
        Student student1 = new Student("John Doe", "john.doe@example.com", "Computer Science", "Software Engineering");
        Student student2 = new Student("Jane Smith", "jane.smith@example.com", "Mathematics", "Applied Mathematics");
        Student student3 = new Student("Bob Wilson", "bob.wilson@example.com", "Physics", "Theoretical Physics");
        
        // Add some sample enrollments to make the schedule more realistic
        // Use course IDs that actually exist in Course Service (1, 2, 3)
        Enrollment enrollment1 = new Enrollment(1L, 1L, EnrollmentStatus.ENROLLED);  // CS101
        enrollment1.setId(1L);
        
        Enrollment enrollment2 = new Enrollment(1L, 2L, EnrollmentStatus.ENROLLED);  // MATH201
        enrollment2.setId(2L);
        
        // Add waitlisted enrollments for student1 using existing course IDs
        Enrollment waitlisted1 = new Enrollment(1L, 3L, EnrollmentStatus.WAITLISTED);  // PHYS101
        waitlisted1.setId(4L);
        
        Enrollment enrollment3 = new Enrollment(2L, 1L, EnrollmentStatus.ENROLLED);  // CS101
        enrollment3.setId(3L);
        
        // Add waitlisted enrollment for student2
        Enrollment waitlisted2 = new Enrollment(2L, 2L, EnrollmentStatus.WAITLISTED);  // MATH201
        waitlisted2.setId(5L);
        
        Enrollment waitlisted3 = new Enrollment(2L, 3L, EnrollmentStatus.WAITLISTED);  // PHYS101
        waitlisted3.setId(6L);
        
        // Add waitlisted enrollment for student3
        Enrollment waitlisted4 = new Enrollment(3L, 1L, EnrollmentStatus.WAITLISTED);  // CS101
        waitlisted4.setId(7L);
        
        Enrollment waitlisted5 = new Enrollment(3L, 2L, EnrollmentStatus.WAITLISTED);  // MATH201
        waitlisted5.setId(8L);
        
        student1.getEnrollments().add(enrollment1);
        student1.getEnrollments().add(enrollment2);
        student1.getEnrollments().add(waitlisted1);
        
        student2.getEnrollments().add(enrollment3);
        student2.getEnrollments().add(waitlisted2);
        student2.getEnrollments().add(waitlisted3);
        
        student3.getEnrollments().add(waitlisted4);
        student3.getEnrollments().add(waitlisted5);
        
        repo.save(student1);
        repo.save(student2);
        repo.save(student3);
        
        System.out.println("Sample data initialized - 3 students created with sample enrollments and waitlisted courses");
        System.out.println("Student 1 (John Doe): Enrolled in CS101, MATH201; Waitlisted for PHYS101");
        System.out.println("Student 2 (Jane Smith): Enrolled in CS101; Waitlisted for MATH201, PHYS101");
        System.out.println("Student 3 (Bob Wilson): Waitlisted for CS101, MATH201");
    }
}
