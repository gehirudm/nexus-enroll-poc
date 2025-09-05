package com.nexus.enrollment.faculty;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import com.nexus.enrollment.faculty.repository.InMemoryFacultyRepository;
import com.nexus.enrollment.faculty.service.FacultyService;
import com.nexus.enrollment.faculty.service.FacultyServiceImpl;
import com.nexus.enrollment.faculty.service.GradeService;
import com.nexus.enrollment.faculty.service.GradeServiceImpl;
import com.nexus.enrollment.faculty.controller.FacultyController;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class FacultyServiceApplication {
    
    private static final int PORT = 8083;
    private static FacultyController controller;
    
    public static void main(String[] args) throws IOException {
        // Initialize repositories
        FacultyRepository facultyRepo = new InMemoryFacultyRepository();
        
        // Initialize services
        FacultyService facultyService = new FacultyServiceImpl(facultyRepo);
        GradeService gradeService = new GradeServiceImpl();
        
        // Initialize controller
        controller = new FacultyController(facultyService, gradeService);
        
        // Initialize with sample data
        initializeSampleData(facultyRepo);
        
        // Start HTTP server
        startHttpServer();
    }
    
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Faculty Service Endpoints
        server.createContext("/faculty", new FacultyHandler());
        server.createContext("/faculty/", new FacultyByIdHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Faculty Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /faculty/{id} - Get faculty by ID");
        System.out.println("  GET /faculty/{id}/courses - Get faculty's assigned courses");
        System.out.println("  GET /faculty/{id}/roster/{courseId} - Get class roster for a course");
        System.out.println("  POST /faculty/{id}/grades - Submit grades for a course");
        System.out.println("  GET /faculty/{id}/grades/{courseId} - Get submitted grades for a course");
        System.out.println("  PUT /faculty/{id}/course-request - Submit course change request");
    }
    
    static class FacultyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\": \"Faculty endpoint\", \"status\": \"success\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class FacultyByIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            
            if (pathParts.length >= 3) {
                String facultyId = pathParts[2];
                
                if ("GET".equals(method)) {
                    if (pathParts.length == 3) {
                        // GET /faculty/{id}
                        response = "{\"message\": \"Get faculty " + facultyId + "\", \"status\": \"success\"}";
                    } else if (pathParts.length == 4) {
                        String action = pathParts[3];
                        if ("courses".equals(action)) {
                            // GET /faculty/{id}/courses
                            response = "{\"message\": \"Get courses for faculty " + facultyId + "\", \"status\": \"success\"}";
                        }
                    } else if (pathParts.length == 5) {
                        String action = pathParts[3];
                        String targetId = pathParts[4];
                        if ("roster".equals(action)) {
                            // GET /faculty/{id}/roster/{courseId}
                            response = "{\"message\": \"Get roster for faculty " + facultyId + " course " + targetId + "\", \"status\": \"success\"}";
                        } else if ("grades".equals(action)) {
                            // GET /faculty/{id}/grades/{courseId}
                            response = "{\"message\": \"Get grades for faculty " + facultyId + " course " + targetId + "\", \"status\": \"success\"}";
                        }
                    }
                } else if ("POST".equals(method) && pathParts.length == 4 && "grades".equals(pathParts[3])) {
                    // POST /faculty/{id}/grades
                    response = "{\"message\": \"Submit grades for faculty " + facultyId + "\", \"status\": \"success\"}";
                } else if ("PUT".equals(method) && pathParts.length == 4 && "course-request".equals(pathParts[3])) {
                    // PUT /faculty/{id}/course-request
                    response = "{\"message\": \"Submit course change request for faculty " + facultyId + "\", \"status\": \"success\"}";
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
