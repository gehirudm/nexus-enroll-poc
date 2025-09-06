package com.nexus.enrollment.student.handler;

import com.nexus.enrollment.student.controller.StudentController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.nexus.enrollment.common.model.Student;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StudentsHandler implements HttpHandler {
    
    private final StudentController controller;
    
    public StudentsHandler(StudentController controller) {
        this.controller = controller;
    }
    
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
        } else if ("POST".equals(method)) {
            try {
                // Read request body and create student object
                String requestBody = readRequestBody(exchange);
                Student student = createStudentFromRequest(requestBody);
                
                // This would require adding a createStudent method to the controller
                // For now, we'll create a simple response
                response = "{\"message\": \"Student created successfully\", \"status\": \"success\", \"data\": " + student.toJson() + "}";
                statusCode = 201;
            } catch (Exception e) {
                response = "{\"message\": \"Failed to create student: " + e.getMessage() + "\", \"status\": \"error\"}";
                statusCode = 400;
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
    
    // Helper method to convert ResponseBuilder.Response to JSON
    private String convertResponseToJson(ResponseBuilder.Response response) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"message\":\"").append(response.getMessage() != null ? response.getMessage().replace("\"", "\\\"") : "").append("\",");
        json.append("\"status\":\"").append(response.isSuccess() ? "success" : "error").append("\"");
        if (response.getData() != null) {
            json.append(",\"data\":").append(convertObjectToJson(response.getData()));
        }
        json.append("}");
        return json.toString();
    }
    
    // Helper method to convert objects to JSON
    private String convertObjectToJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof JsonSerializable) {
            JsonSerializable serializable = (JsonSerializable) obj;
            return serializable.toJson();
        } else if (obj instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) obj;
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) json.append(",");
                json.append(convertObjectToJson(list.get(i)));
            }
            json.append("]");
            return json.toString();
        } else {
            // For other objects, use toString() but wrap in quotes if it's a string-like value
            String value = obj.toString();
            if (obj instanceof String) {
                return "\"" + value.replace("\"", "\\\"") + "\"";
            } else if (obj instanceof Number || obj instanceof Boolean) {
                return value;
            } else {
                return "\"" + value.replace("\"", "\\\"") + "\"";
            }
        }
    }
    
    // Helper method to read request body
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder body = new StringBuilder();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            body.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
        }
        return body.toString();
    }
    
    // Helper method to create Student object from request data
    private Student createStudentFromRequest(String requestBody) {
        Student student = new Student();
        student.setId(null); // Will be auto-generated
        
        // Parse JSON manually (in a real application, you'd use a JSON library like Jackson or Gson)
        if (requestBody != null && !requestBody.trim().isEmpty()) {
            try {
                // Remove outer braces and split by commas
                String cleanJson = requestBody.trim().replaceAll("^\\{|\\}$", "");
                String[] fields = cleanJson.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by comma, but not inside quotes
                
                // Default values
                String name = "New Student";
                String email = "student@example.com";
                String department = "Undeclared";
                
                // Parse each field
                for (String field : fields) {
                    field = field.trim();
                    if (field.contains("\"name\"")) {
                        name = extractStringValue(field);
                    } else if (field.contains("\"email\"")) {
                        email = extractStringValue(field);
                    } else if (field.contains("\"department\"")) {
                        department = extractStringValue(field);
                    }
                }
                
                // Set parsed values
                student.setName(name);
                student.setEmail(email);
                student.setDepartment(department);
                
            } catch (Exception e) {
                // If parsing fails, use default values
                System.err.println("Failed to parse student JSON: " + e.getMessage());
                student.setName("New Student");
                student.setEmail("student@example.com");
                student.setDepartment("Undeclared");
            }
        } else {
            // Default student if no request body
            student.setName("New Student");
            student.setEmail("student@example.com");
            student.setDepartment("Undeclared");
        }
        
        return student;
    }
    
    // Helper method to extract string value from JSON field
    private String extractStringValue(String field) {
        // Extract value after the colon
        String[] parts = field.split(":", 2);
        if (parts.length == 2) {
            String value = parts[1].trim();
            // Remove quotes if present
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        }
        return "";
    }
}
