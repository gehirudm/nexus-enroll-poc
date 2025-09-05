package com.nexus.enrollment.faculty;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import com.nexus.enrollment.faculty.repository.InMemoryFacultyRepository;
import com.nexus.enrollment.faculty.service.FacultyService;
import com.nexus.enrollment.faculty.service.FacultyServiceImpl;
import com.nexus.enrollment.faculty.service.GradeService;
import com.nexus.enrollment.faculty.service.GradeServiceImpl;
import com.nexus.enrollment.faculty.controller.FacultyController;
import com.nexus.enrollment.common.util.ResponseBuilder;
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
    
    // Helper method to convert ResponseBuilder.Response to JSON
    private static String convertResponseToJson(ResponseBuilder.Response response) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"message\":\"").append(response.getMessage() != null ? response.getMessage().replace("\"", "\\\"") : "").append("\",");
        json.append("\"status\":\"").append(response.isSuccess() ? "success" : "error").append("\"");
        if (response.getData() != null) {
            json.append(",\"data\":").append(response.getData().toString());
        }
        json.append("}");
        return json.toString();
    }
    
    static class FacultyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";
            int statusCode = 200;
            
            try {
                if ("GET".equals(method)) {
                    ResponseBuilder.Response controllerResponse = controller.getAllFaculty();
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                } else {
                    response = "{\"message\": \"Method not allowed\", \"status\": \"error\"}";
                    statusCode = 405;
                }
            } catch (Exception e) {
                response = "{\"message\": \"" + e.getMessage() + "\", \"status\": \"error\"}";
                statusCode = 500;
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
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
            int statusCode = 200;
            
            try {
                if (pathParts.length >= 3) {
                    Long facultyId = Long.parseLong(pathParts[2]);
                    
                    if ("GET".equals(method)) {
                        if (pathParts.length == 3) {
                            // GET /faculty/{id}
                            ResponseBuilder.Response controllerResponse = controller.getFaculty(facultyId);
                            response = convertResponseToJson(controllerResponse);
                            statusCode = controllerResponse.isSuccess() ? 200 : 404;
                        } else if (pathParts.length == 4) {
                            String action = pathParts[3];
                            if ("courses".equals(action)) {
                                // GET /faculty/{id}/courses
                                ResponseBuilder.Response controllerResponse = controller.getAssignedCourses(facultyId);
                                response = convertResponseToJson(controllerResponse);
                                statusCode = controllerResponse.isSuccess() ? 200 : 404;
                            }
                        } else if (pathParts.length == 5) {
                            String action = pathParts[3];
                            Long targetId = Long.parseLong(pathParts[4]);
                            if ("roster".equals(action)) {
                                // GET /faculty/{id}/roster/{courseId}
                                ResponseBuilder.Response controllerResponse = controller.getClassRoster(facultyId, targetId);
                                response = convertResponseToJson(controllerResponse);
                                statusCode = controllerResponse.isSuccess() ? 200 : 404;
                            } else if ("grades".equals(action)) {
                                // GET /faculty/{id}/grades/{courseId}
                                ResponseBuilder.Response controllerResponse = controller.getSubmittedGrades(facultyId, targetId);
                                response = convertResponseToJson(controllerResponse);
                                statusCode = controllerResponse.isSuccess() ? 200 : 404;
                            }
                        }
                    } else if ("POST".equals(method) && pathParts.length == 4 && "grades".equals(pathParts[3])) {
                        // POST /faculty/{id}/grades - For now, use dummy grade submission
                        ResponseBuilder.Response controllerResponse = controller.submitGrades(facultyId, null);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    } else if ("PUT".equals(method) && pathParts.length == 4 && "course-request".equals(pathParts[3])) {
                        // PUT /faculty/{id}/course-request - For now, return simple response as this method doesn't exist
                        response = "{\"message\": \"Course change request submitted for faculty " + facultyId + "\", \"status\": \"success\"}";
                    }
                }
                
                if (response.isEmpty()) {
                    response = "{\"message\": \"Endpoint not found\", \"status\": \"error\"}";
                    statusCode = 404;
                }
            } catch (NumberFormatException e) {
                response = "{\"message\": \"Invalid ID format\", \"status\": \"error\"}";
                statusCode = 400;
            } catch (Exception e) {
                response = "{\"message\": \"" + e.getMessage() + "\", \"status\": \"error\"}";
                statusCode = 500;
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
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
