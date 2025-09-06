package com.nexus.enrollment.course.handler;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.common.util.JsonSerializable;
import com.nexus.enrollment.course.controller.CourseController;
import com.nexus.enrollment.course.repository.CourseRepository;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class CoursesHandler implements HttpHandler {
    
    private final CourseController controller;
    private final CourseRepository courseRepository;
    
    public CoursesHandler(CourseController controller, CourseRepository courseRepository) {
        this.controller = controller;
        this.courseRepository = courseRepository;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String[] pathParts = path.split("/");
        String response = "";
        int statusCode = 200;
        
        try {
            if ("GET".equals(method)) {
                if ("/courses".equals(path)) {
                    // GET /courses - Get all courses
                    ResponseBuilder.Response controllerResponse = controller.getAllCourses();
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                } else if ("/courses/available".equals(path)) {
                    // GET /courses/available - Get available courses
                    ResponseBuilder.Response controllerResponse = controller.getAvailableCourses();
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                } else if (path.startsWith("/courses/search")) {
                    // GET /courses/search?keyword={} - Search courses
                    String keyword = null, department = null;
                    if (query != null) {
                        String[] params = query.split("&");
                        for (String param : params) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                if ("keyword".equals(keyValue[0])) {
                                    keyword = keyValue[1];
                                } else if ("department".equals(keyValue[0])) {
                                    department = keyValue[1];
                                }
                            }
                        }
                    }
                    ResponseBuilder.Response controllerResponse = controller.searchCourses(department, keyword);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 400;
                } else if (pathParts.length == 4 && "department".equals(pathParts[2])) {
                    // GET /courses/department/{dept} - Get courses by department
                    String department = pathParts[3];
                    ResponseBuilder.Response controllerResponse = controller.getCoursesByDepartment(department);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 404;
                } else if (pathParts.length == 4 && "instructor".equals(pathParts[2])) {
                    // GET /courses/instructor/{facultyId} - Get courses by instructor
                    Long facultyId = Long.parseLong(pathParts[3]);
                    ResponseBuilder.Response controllerResponse = controller.getCoursesByInstructor(facultyId);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 404;
                } else if (pathParts.length == 3 && Character.isDigit(pathParts[2].charAt(0))) {
                    // GET /courses/{id} - Get course by ID
                    Long courseId = Long.parseLong(pathParts[2]);
                    ResponseBuilder.Response controllerResponse = controller.getCourse(courseId);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 404;
                } else if (pathParts.length == 4 && Character.isDigit(pathParts[2].charAt(0))) {
                    // Course ID based endpoints
                    Long courseId = Long.parseLong(pathParts[2]);
                    String action = pathParts[3];
                    if ("prerequisites".equals(action)) {
                        // GET /courses/{id}/prerequisites - Get course prerequisites
                        try {
                            Course course = courseRepository.findById(courseId).orElse(null);
                            if (course != null && course.getPrerequisites() != null) {
                                response = convertResponseToJson(ResponseBuilder.success("Prerequisites for course " + courseId, course.getPrerequisites()));
                                statusCode = 200;
                            } else if (course != null) {
                                response = convertResponseToJson(ResponseBuilder.success("No prerequisites for course " + courseId, java.util.Collections.emptyList()));
                                statusCode = 200;
                            } else {
                                response = convertResponseToJson(ResponseBuilder.error("Course not found"));
                                statusCode = 404;
                            }
                        } catch (Exception e) {
                            response = convertResponseToJson(ResponseBuilder.error("Error retrieving prerequisites: " + e.getMessage()));
                            statusCode = 500;
                        }
                    } else if ("enrollments".equals(action)) {
                        // GET /courses/{id}/enrollments
                        ResponseBuilder.Response controllerResponse = controller.getEnrollmentCount(courseId);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 404;
                    }
                }
            } else if ("POST".equals(method) && "/courses".equals(path)) {
                // POST /courses - Create new course
                String requestBody = readRequestBody(exchange);
                Course course = createCourseFromRequest(requestBody);
                
                // Save the course using the repository directly
                Course savedCourse = courseRepository.save(course);
                
                if (savedCourse != null) {
                    response = convertResponseToJson(ResponseBuilder.success("Course created successfully", savedCourse));
                    statusCode = 201;
                } else {
                    response = convertResponseToJson(ResponseBuilder.error("Failed to create course"));
                    statusCode = 400;
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
        Course course = new Course();
        course.setId(null); // Will be auto-generated
        
        // Parse JSON manually (in a real application, you'd use a JSON library like Jackson or Gson)
        if (requestBody != null && !requestBody.trim().isEmpty()) {
            try {
                // Remove outer braces and split by commas
                String cleanJson = requestBody.trim().replaceAll("^\\{|\\}$", "");
                String[] fields = cleanJson.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by comma, but not inside quotes
                
                // Default values
                String courseCode = "CS" + System.currentTimeMillis() % 1000; // Generate unique code
                String name = "New Course";
                String description = "Course description";
                String department = "Computer Science";
                int capacity = 30;
                Long instructorId = 1L;
                
                // Parse each field
                for (String field : fields) {
                    field = field.trim();
                    if (field.contains("courseCode")) {
                        courseCode = extractStringValue(field);
                    } else if (field.contains("\"name\"")) {
                        name = extractStringValue(field);
                    } else if (field.contains("description")) {
                        description = extractStringValue(field);
                    } else if (field.contains("department")) {
                        department = extractStringValue(field);
                    } else if (field.contains("capacity")) {
                        try {
                            capacity = Integer.parseInt(extractStringValue(field));
                        } catch (NumberFormatException e) {
                            capacity = 30; // Default
                        }
                    } else if (field.contains("instructorId")) {
                        try {
                            instructorId = Long.parseLong(extractStringValue(field));
                        } catch (NumberFormatException e) {
                            instructorId = 1L; // Default
                        }
                    }
                }
                
                // Set parsed values
                course.setCourseCode(courseCode);
                course.setName(name);
                course.setDescription(description);
                course.setDepartment(department);
                course.setTotalCapacity(capacity);
                course.setAvailableSeats(capacity);
                course.setInstructorId(instructorId);
                
            } catch (Exception e) {
                // If parsing fails, use default values
                System.err.println("Failed to parse course JSON: " + e.getMessage());
                course.setCourseCode("CS" + System.currentTimeMillis() % 1000);
                course.setName("New Course");
                course.setDescription("Course description");
                course.setDepartment("Computer Science");
                course.setTotalCapacity(30);
                course.setAvailableSeats(30);
                course.setInstructorId(1L);
            }
        } else {
            // Default course if no request body
            course.setCourseCode("CS" + System.currentTimeMillis() % 1000);
            course.setName("New Course");
            course.setDescription("Course description");
            course.setDepartment("Computer Science");
            course.setTotalCapacity(30);
            course.setAvailableSeats(30);
            course.setInstructorId(1L);
        }
        
        // Create a default schedule for the new course
        Schedule schedule = new Schedule(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 30), "Room TBD");
        schedule.setId(System.currentTimeMillis()); // Generate a simple ID for the schedule
        course.setSchedule(schedule);
        
        return course;
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
