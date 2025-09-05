package com.nexus.enrollment.admin;

import com.nexus.enrollment.admin.service.AdminService;
import com.nexus.enrollment.admin.service.AdminServiceImpl;
import com.nexus.enrollment.admin.service.ReportService;
import com.nexus.enrollment.admin.service.ReportServiceImpl;
import com.nexus.enrollment.admin.controller.AdminController;
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
    
    static class AdminReportsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String response = "";
            
            if ("GET".equals(method)) {
                if ("/admin/reports/enrollment".equals(path)) {
                    response = "{\"message\": \"Enrollment report with query: " + query + "\", \"status\": \"success\"}";
                } else if ("/admin/reports/faculty-workload".equals(path)) {
                    response = "{\"message\": \"Faculty workload report\", \"status\": \"success\"}";
                } else if ("/admin/reports/course-trends".equals(path)) {
                    response = "{\"message\": \"Course trends report\", \"status\": \"success\"}";
                }
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Report endpoint not found\", \"status\": \"error\"}";
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
    
    static class AdminCoursesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            
            if ("POST".equals(method) && "/admin/courses".equals(path)) {
                response = "{\"message\": \"Create new course\", \"status\": \"success\"}";
            } else if (pathParts.length == 4) {
                String courseId = pathParts[3];
                if ("PUT".equals(method)) {
                    response = "{\"message\": \"Update course " + courseId + "\", \"status\": \"success\"}";
                } else if ("DELETE".equals(method)) {
                    response = "{\"message\": \"Delete course " + courseId + "\", \"status\": \"success\"}";
                }
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Course endpoint not found\", \"status\": \"error\"}";
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
    
    static class AdminStudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            
            if ("GET".equals(method) && "/admin/students".equals(path)) {
                response = "{\"message\": \"Get all students\", \"status\": \"success\"}";
            } else if ("POST".equals(method) && pathParts.length == 6 && "force-enroll".equals(pathParts[4])) {
                String studentId = pathParts[3];
                String courseId = pathParts[5];
                response = "{\"message\": \"Force enroll student " + studentId + " in course " + courseId + "\", \"status\": \"success\"}";
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Student endpoint not found\", \"status\": \"error\"}";
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
    
    static class AdminFacultyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String response = "";
            
            if ("GET".equals(method) && "/admin/faculty".equals(path)) {
                response = "{\"message\": \"Get all faculty\", \"status\": \"success\"}";
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Faculty endpoint not found\", \"status\": \"error\"}";
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
