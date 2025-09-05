package com.nexus.enrollment.faculty;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import com.nexus.enrollment.faculty.repository.InMemoryFacultyRepository;
import com.nexus.enrollment.faculty.service.FacultyService;
import com.nexus.enrollment.faculty.service.FacultyServiceImpl;
import com.nexus.enrollment.faculty.service.GradeService;
import com.nexus.enrollment.faculty.service.GradeServiceImpl;
import com.nexus.enrollment.faculty.controller.FacultyController;
import java.util.Arrays;

public class FacultyServiceApplication {
    
    public static void main(String[] args) {
        // Initialize repositories
        FacultyRepository facultyRepo = new InMemoryFacultyRepository();
        
        // Initialize services
        FacultyService facultyService = new FacultyServiceImpl(facultyRepo);
        GradeService gradeService = new GradeServiceImpl();
        
        // Initialize controller
        FacultyController controller = new FacultyController(facultyService, gradeService);
        
        // Initialize with sample data
        initializeSampleData(facultyRepo);
        
        System.out.println("Faculty Service Application started successfully!");
        
        // Demo usage
        demonstrateService(controller);
    }
    
    private static void initializeSampleData(FacultyRepository repo) {
        Faculty faculty1 = new Faculty("Dr. Alice Johnson", "alice.johnson@university.edu", "Computer Science");
        faculty1.setAssignedCourseIds(Arrays.asList(1L, 2L));
        
        Faculty faculty2 = new Faculty("Prof. Bob Smith", "bob.smith@university.edu", "Mathematics");
        faculty2.setAssignedCourseIds(Arrays.asList(3L));
        
        Faculty faculty3 = new Faculty("Dr. Carol Davis", "carol.davis@university.edu", "Physics");
        faculty3.setAssignedCourseIds(Arrays.asList(4L, 5L));
        
        repo.save(faculty1);
        repo.save(faculty2);
        repo.save(faculty3);
        
        System.out.println("Sample data initialized - 3 faculty members created");
    }
    
    private static void demonstrateService(FacultyController controller) {
        System.out.println("\n=== Faculty Service Demo ===");
        
        // Get all faculty
        var response = controller.getAllFaculty();
        System.out.println("All faculty: " + response.isSuccess());
        
        // Get specific faculty
        var facultyResponse = controller.getFaculty(1L);
        System.out.println("Get faculty 1: " + facultyResponse.isSuccess());
        
        // Get assigned courses
        var coursesResponse = controller.getAssignedCourses(1L);
        System.out.println("Assigned courses: " + coursesResponse.isSuccess());
        
        System.out.println("Demo completed!");
    }
}
