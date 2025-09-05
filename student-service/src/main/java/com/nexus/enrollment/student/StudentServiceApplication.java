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
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
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
        server.createContext("/students", new StudentsHandler());
        server.createContext("/students/", new StudentByIdHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Student Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /students - Get all students");
        System.out.println("  GET /students/{id} - Get student by ID");
        System.out.println("  GET /students/{id}/schedule - Get student's schedule");
        System.out.println("  GET /students/{id}/enrollments - Get student's enrollments");
        System.out.println("  POST /students/{id}/enroll/{courseId} - Enroll in course");
        System.out.println("  DELETE /students/{id}/drop/{courseId} - Drop course");
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
    
    static class StudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";
            int statusCode = 200;
            
            if ("GET".equals(method)) {
                try {
                    ResponseBuilder.Response controllerResponse = controller.getAllStudents();
                    response = convertResponseToJson(controllerResponse);
                } catch (Exception e) {
                    response = "{\"message\": \"" + e.getMessage() + "\", \"status\": \"error\"}";
                    statusCode = 500;
                }
            } else {
                response = "{\"message\": \"Method not allowed\", \"status\": \"error\"}";
                statusCode = 405;
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class StudentByIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            int statusCode = 200;
            
            try {
                if (pathParts.length >= 3) {
                    Long studentId = Long.parseLong(pathParts[2]);
                    
                    if ("GET".equals(method)) {
                        if (pathParts.length == 3) {
                            // GET /students/{id}
                            ResponseBuilder.Response controllerResponse = controller.getStudent(studentId);
                            response = convertResponseToJson(controllerResponse);
                            statusCode = controllerResponse.isSuccess() ? 200 : 404;
                        } else if (pathParts.length == 4) {
                            String action = pathParts[3];
                            if ("schedule".equals(action)) {
                                // GET /students/{id}/schedule
                                ResponseBuilder.Response controllerResponse = controller.getStudentSchedule(studentId);
                                response = convertResponseToJson(controllerResponse);
                                statusCode = controllerResponse.isSuccess() ? 200 : 404;
                            } else if ("enrollments".equals(action)) {
                                // GET /students/{id}/enrollments
                                ResponseBuilder.Response controllerResponse = controller.getStudentEnrollments(studentId);
                                response = convertResponseToJson(controllerResponse);
                                statusCode = controllerResponse.isSuccess() ? 200 : 404;
                            }
                        }
                    } else if ("POST".equals(method) && pathParts.length == 5 && "enroll".equals(pathParts[3])) {
                        // POST /students/{id}/enroll/{courseId}
                        Long courseId = Long.parseLong(pathParts[4]);
                        ResponseBuilder.Response controllerResponse = controller.enrollStudent(studentId, courseId);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    } else if ("DELETE".equals(method) && pathParts.length == 5 && "drop".equals(pathParts[3])) {
                        // DELETE /students/{id}/drop/{courseId}
                        Long courseId = Long.parseLong(pathParts[4]);
                        ResponseBuilder.Response controllerResponse = controller.dropCourse(studentId, courseId);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
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
    
    
    private static void initializeSampleData(StudentRepository repo) {
        Student student1 = new Student("John Doe", "john.doe@example.com", "Computer Science");
        Student student2 = new Student("Jane Smith", "jane.smith@example.com", "Mathematics");
        Student student3 = new Student("Bob Wilson", "bob.wilson@example.com", "Physics");
        
        repo.save(student1);
        repo.save(student2);
        repo.save(student3);
        
        System.out.println("Sample data initialized - 3 students created");
    }
}
