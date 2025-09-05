package course_service;

import common.model.Course;
import common.model.Schedule;
import course_service.repository.CourseRepository;
import course_service.repository.InMemoryCourseRepository;
import course_service.service.CourseService;
import course_service.service.CourseServiceImpl;
import course_service.controller.CourseController;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class CourseServiceApplication {
    
    public static void main(String[] args) {
        // Initialize repositories
        CourseRepository courseRepo = new InMemoryCourseRepository();
        
        // Initialize services
        CourseService courseService = new CourseServiceImpl(courseRepo);
        
        // Initialize controller
        CourseController controller = new CourseController(courseService);
        
        // Initialize with sample data
        initializeSampleData(courseRepo);
        
        System.out.println("Course Service Application started successfully!");
        
        // Demo usage
        demonstrateService(controller);
    }
    
    private static void initializeSampleData(CourseRepository repo) {
        // Create sample schedules
        Schedule schedule1 = new Schedule(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "Room A101");
        Schedule schedule2 = new Schedule(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "Room B202");
        Schedule schedule3 = new Schedule(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "Room C303");
        
        // Create sample courses
        Course course1 = new Course("CS101", "Introduction to Computer Science", 
                "Fundamentals of programming and computer science", 1L, "Computer Science", 30, schedule1);
        Course course2 = new Course("MATH201", "Calculus I", 
                "Differential and integral calculus", 2L, "Mathematics", 25, schedule2);
        Course course3 = new Course("PHYS101", "Physics I", 
                "Mechanics and thermodynamics", 3L, "Physics", 20, schedule3);
        
        repo.save(course1);
        repo.save(course2);
        repo.save(course3);
        
        System.out.println("Sample data initialized - 3 courses created");
    }
    
    private static void demonstrateService(CourseController controller) {
        System.out.println("\n=== Course Service Demo ===");
        
        // Get all courses
        var response = controller.getAllCourses();
        System.out.println("All courses: " + response.isSuccess());
        
        // Get specific course
        var courseResponse = controller.getCourse(1L);
        System.out.println("Get course 1: " + courseResponse.isSuccess());
        
        // Search courses by department
        var deptResponse = controller.getCoursesByDepartment("Computer Science");
        System.out.println("CS courses: " + deptResponse.isSuccess());
        
        // Get available courses
        var availableResponse = controller.getAvailableCourses();
        System.out.println("Available courses: " + availableResponse.isSuccess());
        
        System.out.println("Demo completed!");
    }
}
