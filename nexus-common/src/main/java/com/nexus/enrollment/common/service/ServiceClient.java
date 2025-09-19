package com.nexus.enrollment.common.service;

import com.nexus.enrollment.common.util.ResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

/**
 * Service communication client for inter-microservice HTTP communication
 */
public class ServiceClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, String> serviceBaseUrls;
    
    public ServiceClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        // Configure Jackson ObjectMapper with Java 8 time support (same as WebServer)
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
                
        this.serviceBaseUrls = new HashMap<>();
        initializeServiceUrls();
    }
    
    private void initializeServiceUrls() {
        // Default service URLs - can be configured via environment variables
        serviceBaseUrls.put("student", getEnvOrDefault("STUDENT_SERVICE_URL", "http://localhost:8081"));
        serviceBaseUrls.put("course", getEnvOrDefault("COURSE_SERVICE_URL", "http://localhost:8082"));
        serviceBaseUrls.put("faculty", getEnvOrDefault("FACULTY_SERVICE_URL", "http://localhost:8083"));
        serviceBaseUrls.put("admin", getEnvOrDefault("ADMIN_SERVICE_URL", "http://localhost:8084"));
        serviceBaseUrls.put("notification", getEnvOrDefault("NOTIFICATION_SERVICE_URL", "http://localhost:8085"));
    }
    
    private String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Perform a GET request to another service
     */
    public <T> ServiceResponse<T> get(String serviceName, String endpoint, Class<T> responseType) {
        try {
            String url = buildUrl(serviceName, endpoint);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            return executeRequest(request, responseType);
        } catch (Exception e) {
            return ServiceResponse.error("Failed to communicate with " + serviceName + " service: " + e.getMessage());
        }
    }
    
    /**
     * Perform a POST request to another service
     */
    public <T> ServiceResponse<T> post(String serviceName, String endpoint, Object requestBody, Class<T> responseType) {
        try {
            String url = buildUrl(serviceName, endpoint);
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            return executeRequest(request, responseType);
        } catch (Exception e) {
            return ServiceResponse.error("Failed to communicate with " + serviceName + " service: " + e.getMessage());
        }
    }
    
    /**
     * Perform a PUT request to another service
     */
    public <T> ServiceResponse<T> put(String serviceName, String endpoint, Object requestBody, Class<T> responseType) {
        try {
            String url = buildUrl(serviceName, endpoint);
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            return executeRequest(request, responseType);
        } catch (Exception e) {
            return ServiceResponse.error("Failed to communicate with " + serviceName + " service: " + e.getMessage());
        }
    }
    
    /**
     * Perform a DELETE request to another service
     */
    public <T> ServiceResponse<T> delete(String serviceName, String endpoint, Class<T> responseType) {
        try {
            String url = buildUrl(serviceName, endpoint);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            return executeRequest(request, responseType);
        } catch (Exception e) {
            return ServiceResponse.error("Failed to communicate with " + serviceName + " service: " + e.getMessage());
        }
    }
    
    private String buildUrl(String serviceName, String endpoint) {
        String baseUrl = serviceBaseUrls.get(serviceName);
        if (baseUrl == null) {
            throw new IllegalArgumentException("Unknown service: " + serviceName);
        }
        
        // Ensure proper URL formatting
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        
        return baseUrl + endpoint;
    }
    
    private <T> ServiceResponse<T> executeRequest(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Handle different response status codes
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            // Success response
            try {
                if (responseType == String.class) {
                    @SuppressWarnings("unchecked")
                    T result = (T) response.body();
                    return ServiceResponse.success(result);
                } else if (responseType == Void.class) {
                    return ServiceResponse.success(null);
                } else {
                    // First, try to extract data from our standard API response format
                    String responseBody = response.body();
                    
                    // More robust check for standard response format
                    if (responseBody != null && responseBody.trim().startsWith("{") && 
                        responseBody.contains("\"success\"") && responseBody.contains("\"data\"")) {
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                            
                            if (responseMap != null) {
                                Object successValue = responseMap.get("success");
                                if (Boolean.TRUE.equals(successValue)) {
                                    Object dataObject = responseMap.get("data");
                                    if (dataObject != null) {
                                        // Use Jackson's convertValue for efficient type conversion
                                        T data = objectMapper.convertValue(dataObject, responseType);
                                        return ServiceResponse.success(data);
                                    } else {
                                        return ServiceResponse.success(null);
                                    }
                                } else {
                                    // Handle error response
                                    Object messageValue = responseMap.get("message");
                                    String errorMessage = messageValue != null ? messageValue.toString() : "Unknown error";
                                    return ServiceResponse.error(errorMessage);
                                }
                            } else {
                                // responseMap is null - treat as error
                                return ServiceResponse.error("Invalid response format");
                            }
                        } catch (Exception e) {
                            // If standard format parsing fails, try direct parsing
                            T data = objectMapper.readValue(responseBody, responseType);
                            return ServiceResponse.success(data);
                        }
                    } else {
                        // Direct parsing for non-standard response format
                        T data = objectMapper.readValue(responseBody, responseType);
                        return ServiceResponse.success(data);
                    }
                }
            } catch (JsonProcessingException e) {
                return ServiceResponse.error("Failed to parse response from service: " + e.getMessage());
            }
        } else {
            // Error response
            String responseBody = response.body();
            try {
                // Try to parse as our standard error format
                if (responseBody != null && responseBody.trim().startsWith("{")) {
                    ResponseBuilder.Response errorResponse = objectMapper.readValue(responseBody, ResponseBuilder.Response.class);
                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        return ServiceResponse.error(errorResponse.getMessage());
                    }
                }
            } catch (JsonProcessingException e) {
                // Log parsing failure for debugging (could be enhanced with proper logging)
                System.err.println("Failed to parse error response as JSON: " + e.getMessage());
            }
            
            // Fallback to raw response with status code
            String errorMessage = "Service responded with status " + response.statusCode();
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                errorMessage += ": " + responseBody;
            }
            return ServiceResponse.error(errorMessage);
        }
    }
}
