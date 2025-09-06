package com.nexus.enrollment.course;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.common.model.Prerequisite;
import com.nexus.enrollment.common.web.WebServer;
import com.nexus.enrollment.course.repository.CourseRepository;
import com.nexus.enrollment.course.repository.InMemoryCourseRepository;
import com.nexus.enrollment.course.service.CourseService;
import com.nexus.enrollment.course.handler.CoursesHandler;
import io.javalin.Javalin;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class CourseServiceApplication {
    
    private static final int PORT = 8082;
    private static CourseRepository courseRepository;
    private static CoursesHandler coursesHandler;
    
    public static void main(String[] args) {
        // Initialize repositories
        courseRepository = new InMemoryCourseRepository();
        
        // Initialize services
        CourseService courseService = new CourseService(courseRepository);
        
        // Initialize handler
        coursesHandler = new CoursesHandler(courseService, courseRepository);
        
        // Initialize with sample data
        initializeSampleData(courseRepository);
        
        // Start Javalin server
        startJavalinServer();
    }
    
    private static void startJavalinServer() {
        Javalin app = WebServer.createAndConfigureServer();
        
        app.start(PORT);
        
        System.out.println("Course Service started on port " + PORT);
        System.out.println("Available endpoints:");
        
        // Course Service Endpoints using CoursesHandler methods
        app.get("/courses", coursesHandler::getAllCourses);
        app.get("/courses/{id}", coursesHandler::getCourseById);
        app.get("/courses/department/{dept}", coursesHandler::getCoursesByDepartment);
        app.get("/courses/instructor/{facultyId}", coursesHandler::getCoursesByInstructor);
        app.get("/courses/search", coursesHandler::searchCourses);
        app.get("/courses/available", coursesHandler::getAvailableCourses);
        app.get("/courses/{id}/prerequisites", coursesHandler::getCoursePrerequisites);
        app.get("/courses/{id}/enrollments", coursesHandler::getCourseEnrollments);
        app.post("/courses", coursesHandler::createCourse);
        
        System.out.println("  GET /courses - Get all courses");
        System.out.println("  GET /courses/{id} - Get course by ID");
        System.out.println("  GET /courses/department/{dept} - Get courses by department");
        System.out.println("  GET /courses/instructor/{facultyId} - Get courses by instructor");
        System.out.println("  GET /courses/search?keyword={} - Search courses by keyword");
        System.out.println("  GET /courses/available - Get available courses");
        System.out.println("  GET /courses/{id}/prerequisites - Get course prerequisites");
        System.out.println("  GET /courses/{id}/enrollments - Get enrolled students count");
        System.out.println("  POST /courses - Create new course");
    }
    
    private static void initializeSampleData(CourseRepository repo) {
        // Create sample schedules with IDs
        Schedule schedule1 = new Schedule(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "Room A101");
        schedule1.setId(1L);
        
        Schedule schedule2 = new Schedule(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "Room B202");
        schedule2.setId(2L);
        
        Schedule schedule3 = new Schedule(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "Room C303");
        schedule3.setId(3L);
        
        // Create sample courses
        Course course1 = new Course("CS101", "Introduction to Computer Science", 
                "Fundamentals of programming and computer science", 1L, "Computer Science", 30, schedule1);
        Course course2 = new Course("MATH201", "Calculus I", 
                "Differential and integral calculus", 2L, "Mathematics", 25, schedule2);
        Course course3 = new Course("PHYS101", "Physics I", 
                "Mechanics and thermodynamics", 3L, "Physics", 20, schedule3);
        
        // Add some prerequisites to demonstrate the functionality
        // For course2 (Calculus I), require CS101 with minimum grade C
        Prerequisite prereq1 = new Prerequisite(2L, 1L, "C");
        prereq1.setId(1L);
        course2.getPrerequisites().add(prereq1);
        
        // For course3 (Physics I), require both CS101 and MATH201
        Prerequisite prereq2 = new Prerequisite(3L, 1L, "B");
        prereq2.setId(2L);
        Prerequisite prereq3 = new Prerequisite(3L, 2L, "C");
        prereq3.setId(3L);
        course1.getPrerequisites().add(prereq2);
        course1.getPrerequisites().add(prereq3);
        
        repo.save(course1);
        repo.save(course2);
        repo.save(course3);
        
        System.out.println("Sample data initialized - 3 courses created");
    }
}
