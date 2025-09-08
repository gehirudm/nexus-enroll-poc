package com.nexus.enrollment.common.service;

import com.nexus.enrollment.common.util.ResponseBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
    private final Gson gson;
    private final Map<String, String> serviceBaseUrls;
    
    public ServiceClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
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
            String jsonBody = gson.toJson(requestBody);
            
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
            String jsonBody = gson.toJson(requestBody);
            
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
    
    @SuppressWarnings("unchecked")
    private <T> ServiceResponse<T> executeRequest(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Handle different response status codes
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            // Success response
            try {
                if (responseType == String.class) {
                    return ServiceResponse.success((T) response.body());
                } else if (responseType == Void.class) {
                    return ServiceResponse.success(null);
                } else {
                    // Try to parse as ResponseBuilder.Response first (our standard API response format)
                    ResponseBuilder.Response apiResponse = gson.fromJson(response.body(), ResponseBuilder.Response.class);
                    if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Convert the data to the expected type
                        T data = gson.fromJson(gson.toJson(apiResponse.getData()), responseType);
                        return ServiceResponse.success(data);
                    } else {
                        // Direct parsing
                        T data = gson.fromJson(response.body(), responseType);
                        return ServiceResponse.success(data);
                    }
                }
            } catch (JsonSyntaxException e) {
                return ServiceResponse.error("Failed to parse response from service: " + e.getMessage());
            }
        } else {
            // Error response
            try {
                ResponseBuilder.Response errorResponse = gson.fromJson(response.body(), ResponseBuilder.Response.class);
                if (errorResponse != null && errorResponse.getMessage() != null) {
                    return ServiceResponse.error(errorResponse.getMessage());
                }
            } catch (JsonSyntaxException ignored) {
                // If we can't parse as our standard error format, use the raw response
            }
            
            return ServiceResponse.error("Service responded with status " + response.statusCode() + ": " + response.body());
        }
    }
}
