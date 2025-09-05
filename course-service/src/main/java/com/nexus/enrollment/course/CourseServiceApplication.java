package com.nexus.enrollment.course;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.course.repository.CourseRepository;
import com.nexus.enrollment.course.repository.InMemoryCourseRepository;
import com.nexus.enrollment.course.service.CourseService;
import com.nexus.enrollment.course.service.CourseServiceImpl;
import com.nexus.enrollment.course.controller.CourseController;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class CourseServiceApplication {
    
    private static final int PORT = 8082;
    private static CourseController controller;
    
    public static void main(String[] args) throws IOException {
        // Initialize repositories
        CourseRepository courseRepo = new InMemoryCourseRepository();
        
        // Initialize services
        CourseService courseService = new CourseServiceImpl(courseRepo);
        
        // Initialize controller
        controller = new CourseController(courseService);
        
        // Initialize with sample data
        initializeSampleData(courseRepo);
        
        // Start HTTP server
        startHttpServer();
    }
    
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Course Service Endpoints
        server.createContext("/courses", new CoursesHandler());
        server.createContext("/courses/", new CourseByIdHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Course Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /courses - Get all courses");
        System.out.println("  GET /courses/{id} - Get course by ID");
        System.out.println("  GET /courses/department/{dept} - Get courses by department");
        System.out.println("  GET /courses/instructor/{facultyId} - Get courses by instructor");
        System.out.println("  GET /courses/search?keyword={} - Search courses by keyword");
        System.out.println("  GET /courses/available - Get available courses");
        System.out.println("  GET /courses/{id}/prerequisites - Get course prerequisites");
        System.out.println("  GET /courses/{id}/enrollments - Get enrolled students count");
    }
    
    static class CoursesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String response = "";
            
            if ("GET".equals(method)) {
                if ("/courses".equals(path)) {
                    response = "{\"message\": \"Get all courses\", \"status\": \"success\"}";
                } else if ("/courses/available".equals(path)) {
                    response = "{\"message\": \"Get available courses\", \"status\": \"success\"}";
                } else if (path.startsWith("/courses/search") && query != null) {
                    response = "{\"message\": \"Search courses with query: " + query + "\", \"status\": \"success\"}";
                }
            } else if ("POST".equals(method) && "/courses".equals(path)) {
                response = "{\"message\": \"Create new course\", \"status\": \"success\"}";
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Endpoint not found\", \"status\": \"error\"}";
                exchange.sendResponseHeaders(404, response.getBytes().length);
            } else {
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class CourseByIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            
            if ("GET".equals(method) && pathParts.length >= 3) {
                if (pathParts.length == 3) {
                    // GET /courses/{id}
                    String courseId = pathParts[2];
                    response = "{\"message\": \"Get course " + courseId + "\", \"status\": \"success\"}";
                } else if (pathParts.length == 4) {
                    String courseId = pathParts[2];
                    String action = pathParts[3];
                    if ("prerequisites".equals(action)) {
                        response = "{\"message\": \"Get prerequisites for course " + courseId + "\", \"status\": \"success\"}";
                    } else if ("enrollments".equals(action)) {
                        response = "{\"message\": \"Get enrollment count for course " + courseId + "\", \"status\": \"success\"}";
                    }
                } else if (pathParts.length == 4 && "department".equals(pathParts[2])) {
                    // GET /courses/department/{dept}
                    String department = pathParts[3];
                    response = "{\"message\": \"Get courses for department " + department + "\", \"status\": \"success\"}";
                } else if (pathParts.length == 4 && "instructor".equals(pathParts[2])) {
                    // GET /courses/instructor/{facultyId}
                    String facultyId = pathParts[3];
                    response = "{\"message\": \"Get courses for instructor " + facultyId + "\", \"status\": \"success\"}";
                }
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Endpoint not found\", \"status\": \"error\"}";
                exchange.sendResponseHeaders(404, response.getBytes().length);
            } else {
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
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
