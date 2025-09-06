package com.nexus.enrollment.admin.handler;

import com.nexus.enrollment.admin.controller.AdminController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.nexus.enrollment.common.model.Course;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AdminCoursesHandler implements HttpHandler {
    
    private final AdminController controller;
    
    public AdminCoursesHandler(AdminController controller) {
        this.controller = controller;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        String response = "";
        int statusCode = 200;
        
        try {
            if ("POST".equals(method) && "/admin/courses".equals(path)) {
                // Read request body and create course object
                String requestBody = readRequestBody(exchange);
                Course course = createCourseFromRequest(requestBody);
                ResponseBuilder.Response controllerResponse = controller.createCourse(course);
                response = convertResponseToJson(controllerResponse);
                statusCode = controllerResponse.isSuccess() ? 201 : 400;
            } else if (pathParts.length == 4) {
                Long courseId = Long.parseLong(pathParts[3]);
                if ("PUT".equals(method)) {
                    // For PUT requests, also read the request body
                    String requestBody = readRequestBody(exchange);
                    Course course = createCourseFromRequest(requestBody);
                    course.setId(courseId); // Set the ID from the path
                    ResponseBuilder.Response controllerResponse = controller.updateCourse(courseId, course);
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
    
    // Helper method to create Course object from request data
    private Course createCourseFromRequest(String requestBody) {
        // For demonstration, create a sample course
        // In a real application, you would parse JSON from requestBody
        Course course = new Course();
        course.setId(null); // Will be auto-generated
        course.setCourseCode("CS101");
        course.setName("Introduction to Computer Science");
        course.setDescription("Basic computer science concepts");
        course.setDepartment("Computer Science");
        course.setTotalCapacity(30);
        course.setAvailableSeats(30);
        course.setInstructorId(1L);
        return course;
    }
}
