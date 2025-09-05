package com.nexus.enrollment.student;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.student.repository.StudentRepository;
import com.nexus.enrollment.student.repository.InMemoryStudentRepository;
import com.nexus.enrollment.student.service.StudentService;
import com.nexus.enrollment.student.service.StudentServiceImpl;
import com.nexus.enrollment.student.service.EnrollmentService;
import com.nexus.enrollment.student.service.EnrollmentServiceImpl;
import com.nexus.enrollment.student.validator.EnrollmentValidator;
import com.nexus.enrollment.student.validator.PrerequisiteValidator;
import com.nexus.enrollment.student.controller.StudentController;
import java.util.Arrays;
import java.util.List;

public class StudentServiceApplication {
    
    public static void main(String[] args) {
        // Initialize repositories
        StudentRepository studentRepo = new InMemoryStudentRepository();
        
        // Initialize validators
        List<EnrollmentValidator> validators = Arrays.asList(new PrerequisiteValidator());
        
        // Initialize services
        StudentService studentService = new StudentServiceImpl(studentRepo);
        EnrollmentService enrollmentService = new EnrollmentServiceImpl(studentRepo, validators);
        
        // Initialize controller
        StudentController controller = new StudentController(studentService, enrollmentService);
        
        // Initialize with sample data
        initializeSampleData(studentRepo);
        
        System.out.println("Student Service Application started successfully!");
        
        // Demo usage
        demonstrateService(controller);
    }
    
    private static void initializeSampleData(StudentRepository repo) {
        Student student1 = new Student("John Doe", "john.doe@example.com", "Computer Science");
        Student student2 = new Student("Jane Smith", "jane.smith@example.com", "Mathematics");
        Student student3 = new Student("Bob Wilson", "bob.wilson@example.com", "Physics");
        
        repo.save(student1);
        repo.save(student2);
        repo.save(student3);
        
        System.out.println("Sample data initialized - 3 students created");
    }
    
    private static void demonstrateService(StudentController controller) {
        System.out.println("\n=== Student Service Demo ===");
        
        // Get all students
        var response = controller.getAllStudents();
        System.out.println("All students: " + response.isSuccess());
        
        // Get specific student
        var studentResponse = controller.getStudent(1L);
        System.out.println("Get student 1: " + studentResponse.isSuccess());
        
        // Create new student
        Student newStudent = new Student("Alice Brown", "alice.brown@example.com", "Chemistry");
        var createResponse = controller.createStudent(newStudent);
        System.out.println("Create student: " + createResponse.isSuccess());
        
        System.out.println("Demo completed!");
    }
}
