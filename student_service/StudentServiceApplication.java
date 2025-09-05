package student_service;

import common.model.Student;
import student_service.repository.StudentRepository;
import student_service.repository.InMemoryStudentRepository;
import student_service.service.StudentService;
import student_service.service.StudentServiceImpl;
import student_service.service.EnrollmentService;
import student_service.service.EnrollmentServiceImpl;
import student_service.validator.EnrollmentValidator;
import student_service.validator.PrerequisiteValidator;
import student_service.controller.StudentController;
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
