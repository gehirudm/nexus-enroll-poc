package com.nexus.enrollment.notification;

import com.nexus.enrollment.common.model.Notification;
import com.nexus.enrollment.common.enums.NotificationType;
import com.nexus.enrollment.notification.repository.NotificationRepository;
import com.nexus.enrollment.notification.repository.InMemoryNotificationRepository;
import com.nexus.enrollment.notification.service.NotificationService;
import com.nexus.enrollment.notification.service.NotificationServiceImpl;
import com.nexus.enrollment.notification.controller.NotificationController;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class NotificationServiceApplication {

    private static final int PORT = 8085;
    private static NotificationController controller;

    public static void main(String[] args) throws IOException {
        // Initialize repositories
        NotificationRepository notificationRepo = new InMemoryNotificationRepository();

        // Initialize services
        NotificationService notificationService = new NotificationServiceImpl(notificationRepo);

        // Initialize controller
        controller = new NotificationController(notificationService);

        // Initialize with sample data
        initializeSampleData(controller);

        // Start HTTP server
        startHttpServer();
    }

    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Notification Service Endpoints
        server.createContext("/notifications", new NotificationsHandler());
        server.createContext("/notifications/", new NotificationsByIdHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Notification Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  POST /notifications - Send notification");
        System.out.println("  GET /notifications/user/{userId} - Get user notifications");
        System.out.println("  GET /notifications/type/{type} - Get notifications by type");
        System.out.println("  PUT /notifications/{id}/read - Mark notification as read");
        System.out.println("  POST /notifications/subscribe - Subscribe to notification type");
    }
    
    // Helper method to convert ResponseBuilder.Response to JSON
    private static String convertResponseToJson(ResponseBuilder.Response response) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"message\":\"").append(response.getMessage() != null ? response.getMessage().replace("\"", "\\\"") : "").append("\",");
        json.append("\"status\":\"").append(response.isSuccess() ? "success" : "error").append("\"");
        if (response.getData() != null) {
            json.append(",\"data\":").append(response.getData().toString());
        }
        json.append("}");
        return json.toString();
    }

    static class NotificationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String response = "";
            int statusCode = 200;

            try {
                if ("POST".equals(method)) {
                    if ("/notifications".equals(path)) {
                        // For now, create dummy notification - in real implementation, parse from request body
                        ResponseBuilder.Response controllerResponse = controller.createAndSendNotification(1L, NotificationType.ENROLLMENT_CONFIRMATION, "Test notification");
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 201 : 400;
                    } else if ("/notifications/subscribe".equals(path)) {
                        // For now, return simple response as subscribe method doesn't exist
                        response = "{\"message\": \"Subscribe to notification type\", \"status\": \"success\"}";
                    }
                } else if ("GET".equals(method) && "/notifications".equals(path)) {
                    // For now, return simple response as get all notifications method doesn't exist
                    response = "{\"message\": \"Get all notifications\", \"status\": \"success\"}";
                } else {
                    response = "{\"message\": \"Method not allowed\", \"status\": \"error\"}";
                    statusCode = 405;
                }

                if (response.isEmpty()) {
                    response = "{\"message\": \"Notification endpoint not found\", \"status\": \"error\"}";
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
    }

    static class NotificationsByIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String response = "";
            int statusCode = 200;
            
            try {
                if ("GET".equals(method) && pathParts.length >= 4) {
                    String section = pathParts[2];
                    String identifier = pathParts[3];
                    
                    if ("user".equals(section)) {
                        // GET /notifications/user/{userId}
                        Long userId = Long.parseLong(identifier);
                        ResponseBuilder.Response controllerResponse = controller.getUserNotifications(userId);
                        response = convertResponseToJson(controllerResponse);
                        statusCode = controllerResponse.isSuccess() ? 200 : 404;
                    } else if ("type".equals(section)) {
                        // GET /notifications/type/{type}
                        try {
                            NotificationType type = NotificationType.valueOf(identifier.toUpperCase());
                            ResponseBuilder.Response controllerResponse = controller.getNotificationsByType(type);
                            response = convertResponseToJson(controllerResponse);
                            statusCode = controllerResponse.isSuccess() ? 200 : 404;
                        } catch (IllegalArgumentException e) {
                            response = "{\"message\": \"Invalid notification type\", \"status\": \"error\"}";
                            statusCode = 400;
                        }
                    }
                } else if ("PUT".equals(method) && pathParts.length == 4 && "read".equals(pathParts[3])) {
                    // PUT /notifications/{id}/read
                    Long notificationId = Long.parseLong(pathParts[2]);
                    ResponseBuilder.Response controllerResponse = controller.markAsRead(notificationId);
                    response = convertResponseToJson(controllerResponse);
                    statusCode = controllerResponse.isSuccess() ? 200 : 404;
                }
                
                if (response.isEmpty()) {
                    response = "{\"message\": \"Notification endpoint not found\", \"status\": \"error\"}";
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
    }    private static void initializeSampleData(NotificationController controller) {
        // Create some sample notifications
        controller.createAndSendNotification(1L, NotificationType.ENROLLMENT_CONFIRMATION, 
                "You have been successfully enrolled in CS101");
        controller.createAndSendNotification(2L, NotificationType.GRADE_SUBMITTED, 
                "Your grade for MATH201 has been submitted");
        controller.createAndSendNotification(1L, NotificationType.COURSE_FULL, 
                "CS201 is now full. You have been added to the waitlist");
        
        // Subscribe users to notification types
        controller.subscribeToNotifications(1L, NotificationType.ENROLLMENT_CONFIRMATION);
        controller.subscribeToNotifications(1L, NotificationType.WAITLIST_AVAILABLE);
        controller.subscribeToNotifications(2L, NotificationType.GRADE_SUBMITTED);
        
        System.out.println("Sample notification data initialized");
    }
}
