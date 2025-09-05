package com.nexus.enrollment.admin;

import com.nexus.enrollment.admin.service.AdminService;
import com.nexus.enrollment.admin.service.AdminServiceImpl;
import com.nexus.enrollment.admin.service.ReportService;
import com.nexus.enrollment.admin.service.ReportServiceImpl;
import com.nexus.enrollment.admin.controller.AdminController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class AdminServiceApplication {
    
    private static final int PORT = 8084;
    private static AdminController controller;
    
    public static void main(String[] args) throws IOException {
        // Initialize services
        AdminService adminService = new AdminServiceImpl();
        ReportService reportService = new ReportServiceImpl();
        
        // Initialize controller
        controller = new AdminController(adminService, reportService);
        
        // Start HTTP server
        startHttpServer();
    }
    
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Admin Service Endpoints
        server.createContext("/admin/reports", new AdminReportsHandler());
        server.createContext("/admin/courses", new AdminCoursesHandler());
        server.createContext("/admin/students", new AdminStudentsHandler());
        server.createContext("/admin/faculty", new AdminFacultyHandler());
        server.createContext("/admin/", new AdminHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Admin Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /admin/reports/enrollment?department={}&semester={} - Enrollment report");
        System.out.println("  GET /admin/reports/faculty-workload - Faculty workload report");
        System.out.println("  GET /admin/reports/course-trends - Course popularity trends");
        System.out.println("  POST /admin/courses - Create new course");
        System.out.println("  PUT /admin/courses/{id} - Update course");
        System.out.println("  DELETE /admin/courses/{id} - Delete course");
        System.out.println("  POST /admin/students/{id}/force-enroll/{courseId} - Force enroll student");
        System.out.println("  GET /admin/students - Get all students");
        System.out.println("  GET /admin/faculty - Get all faculty");
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
    
    static class AdminReportsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String response = "";
            int statusCode = 200;
            
            try {
                if ("GET".equals(method)) {
                    if ("/admin/reports/enrollment".equals(path)) {
                        // Parse query parameters for department and semester
                        String department = null, semester = null;
                        if (query != null) {
                            String[] params = query.split("&");
                            for (String param : params) {
                                String[] keyValue = param.split("=");
                                if (keyValue.length == 2) {
                                    if ("department".equals(keyValue[0])) {
                                        department = keyValue[1];
                                    } else if ("semester".equals(keyValue[0])) {
                                        semester = keyValue[1];
                                    }
                                }
                            }
                        }
                        ResponseBuilder.Response controllerResponse = controller.generateEnrollmentReport(department, semester);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    } else if ("/admin/reports/faculty-workload".equals(path)) {
                        ResponseBuilder.Response controllerResponse = controller.generateFacultyWorkloadReport();
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    } else if ("/admin/reports/course-trends".equals(path)) {
                        ResponseBuilder.Response controllerResponse = controller.generateCourseTrendsReport();
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    }
                }
                
                if (response.isEmpty()) {
                    response = "{\"message\": \"Report endpoint not found\", \"status\": \"error\"}";
                    statusCode = 404;
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
    
    static class AdminCoursesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            int statusCode = 200;
            
            try {
                if ("POST".equals(method) && "/admin/courses".equals(path)) {
                    // For now, create a dummy course object - in real implementation, parse from request body
                    ResponseBuilder.Response controllerResponse = controller.createCourse(null);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 201 : 400;
                } else if (pathParts.length == 4) {
                    Long courseId = Long.parseLong(pathParts[3]);
                    if ("PUT".equals(method)) {
                        ResponseBuilder.Response controllerResponse = controller.updateCourse(courseId, null);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    } else if ("DELETE".equals(method)) {
                        ResponseBuilder.Response controllerResponse = controller.deleteCourse(courseId);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    }
                }
                
                if (response.isEmpty()) {
                    response = "{\"message\": \"Course endpoint not found\", \"status\": \"error\"}";
                    statusCode = 404;
                }
            } catch (NumberFormatException e) {
                response = "{\"message\": \"Invalid course ID format\", \"status\": \"error\"}";
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
    
    static class AdminStudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            int statusCode = 200;
            
            try {
                if ("GET".equals(method) && "/admin/students".equals(path)) {
                    ResponseBuilder.Response controllerResponse = controller.getAllStudents();
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                } else if ("POST".equals(method) && pathParts.length == 6 && "force-enroll".equals(pathParts[4])) {
                    Long studentId = Long.parseLong(pathParts[3]);
                    Long courseId = Long.parseLong(pathParts[5]);
                    ResponseBuilder.Response controllerResponse = controller.forceEnrollStudent(studentId, courseId);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                }
                
                if (response.isEmpty()) {
                    response = "{\"message\": \"Student endpoint not found\", \"status\": \"error\"}";
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
    
    static class AdminFacultyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String response = "";
            int statusCode = 200;
            
            try {
                if ("GET".equals(method) && "/admin/faculty".equals(path)) {
                    ResponseBuilder.Response controllerResponse = controller.getAllFaculty();
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                }
                
                if (response.isEmpty()) {
                    response = "{\"message\": \"Faculty endpoint not found\", \"status\": \"error\"}";
                    statusCode = 404;
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
    
    static class AdminHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\": \"Admin Service\", \"status\": \"success\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
