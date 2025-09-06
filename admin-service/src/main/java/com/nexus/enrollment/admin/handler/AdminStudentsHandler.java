package com.nexus.enrollment.admin.handler;

import com.nexus.enrollment.admin.controller.AdminController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class AdminStudentsHandler implements HttpHandler {
    
    private final AdminController controller;
    
    public AdminStudentsHandler(AdminController controller) {
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
}
