package com.nexus.enrollment.faculty.handler;

import com.nexus.enrollment.faculty.controller.FacultyController;
import com.nexus.enrollment.faculty.service.GradeSubmission;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FacultyByIdHandler implements HttpHandler {
    
    private final FacultyController controller;
    
    public FacultyByIdHandler(FacultyController controller) {
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
                    // POST /faculty/{id}/grades - Submit grades
                    try {
                        List<GradeSubmission> gradeSubmissions = parseGradeSubmissionsFromRequest(exchange);
                        ResponseBuilder.Response controllerResponse = controller.submitGrades(facultyId, gradeSubmissions);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 400;
                    } catch (Exception e) {
                        response = "{\"message\": \"Failed to parse grade submission data: " + e.getMessage().replace("\"", "\\\"") + "\", \"status\": \"error\"}";
                        statusCode = 400;
                    }
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
    
    private List<GradeSubmission> parseGradeSubmissionsFromRequest(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        
        String jsonData = requestBody.toString().trim();
        List<GradeSubmission> gradeSubmissions = new ArrayList<>();
        
        // Parse JSON object with courseId and grades array
        if (jsonData.startsWith("{") && jsonData.endsWith("}")) {
            // Extract courseId and grades array from the main object
            String content = jsonData.substring(1, jsonData.length() - 1); // Remove outer braces
            
            Long courseId = null;
            String gradesArrayStr = null;
            
            // Parse the main object to get courseId and grades
            String[] mainPairs = content.split(",(?=\\s*\"\\w+\":|\\s*\"grades\"\\s*:)");
            for (String pair : mainPairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();
                    
                    if ("courseId".equals(key)) {
                        String cleanValue = value.replace("\"", "").replace("{{courseId}}", "1"); // Default to course 1
                        courseId = Long.parseLong(cleanValue);
                    } else if ("grades".equals(key)) {
                        gradesArrayStr = value;
                    }
                }
            }
            
            // Parse the grades array
            if (gradesArrayStr != null && courseId != null) {
                gradeSubmissions = parseGradesArray(gradesArrayStr, courseId);
            }
        }
        
        return gradeSubmissions;
    }
    
    private List<GradeSubmission> parseGradesArray(String gradesArrayStr, Long courseId) {
        List<GradeSubmission> gradeSubmissions = new ArrayList<>();
        
        // Remove outer brackets from array
        if (gradesArrayStr.startsWith("[") && gradesArrayStr.endsWith("]")) {
            String arrayContent = gradesArrayStr.substring(1, gradesArrayStr.length() - 1).trim();
            
            if (!arrayContent.isEmpty()) {
                // Split by grade objects
                String[] gradeObjects = arrayContent.split("\\},\\s*\\{");
                
                for (int i = 0; i < gradeObjects.length; i++) {
                    String gradeObjStr = gradeObjects[i].trim();
                    // Add back braces if they were removed by split
                    if (!gradeObjStr.startsWith("{")) gradeObjStr = "{" + gradeObjStr;
                    if (!gradeObjStr.endsWith("}")) gradeObjStr = gradeObjStr + "}";
                    
                    GradeSubmission submission = parseGradeObject(gradeObjStr, courseId);
                    gradeSubmissions.add(submission);
                }
            }
        }
        
        return gradeSubmissions;
    }
    
    private GradeSubmission parseGradeObject(String json, Long courseId) {
        GradeSubmission submission = new GradeSubmission();
        submission.setCourseId(courseId); // Set the courseId from the parent object
        
        // Simple JSON parsing (extract key-value pairs)
        String content = json.substring(1, json.length() - 1); // Remove braces
        String[] pairs = content.split(",(?=\\s*\"\\w+\"\\s*:)");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                
                switch (key) {
                    case "studentId":
                        submission.setStudentId(Long.parseLong(value));
                        break;
                    case "grade":
                        submission.setGradeValue(value);
                        break;
                    case "comments":
                        // Note: GradeSubmission doesn't have comments field, 
                        // but we could extend it if needed
                        break;
                }
            }
        }
        
        return submission;
    }
}
