package com.nexus.enrollment.faculty.handler;

import com.nexus.enrollment.faculty.controller.FacultyController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class FacultyHandler implements HttpHandler {
    
    private final FacultyController controller;
    
    public FacultyHandler(FacultyController controller) {
        this.controller = controller;
    }
    
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
