package com.nexus.enrollment.admin.handler;

import com.nexus.enrollment.admin.controller.AdminController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class AdminReportsHandler implements HttpHandler {
    
    private final AdminController controller;
    
    public AdminReportsHandler(AdminController controller) {
        this.controller = controller;
    }
    
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
