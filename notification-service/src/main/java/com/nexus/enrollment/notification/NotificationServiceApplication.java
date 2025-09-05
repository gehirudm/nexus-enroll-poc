package com.nexus.enrollment.notification;

import com.nexus.enrollment.common.model.Notification;
import com.nexus.enrollment.common.enums.NotificationType;
import com.nexus.enrollment.notification.repository.NotificationRepository;
import com.nexus.enrollment.notification.repository.InMemoryNotificationRepository;
import com.nexus.enrollment.notification.service.NotificationService;
import com.nexus.enrollment.notification.service.NotificationServiceImpl;
import com.nexus.enrollment.notification.controller.NotificationController;
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
    
    static class NotificationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String response = "";
            
            if ("POST".equals(method)) {
                if ("/notifications".equals(path)) {
                    response = "{\"message\": \"Send notification\", \"status\": \"success\"}";
                } else if ("/notifications/subscribe".equals(path)) {
                    response = "{\"message\": \"Subscribe to notification type\", \"status\": \"success\"}";
                }
            } else if ("GET".equals(method) && "/notifications".equals(path)) {
                response = "{\"message\": \"Get all notifications\", \"status\": \"success\"}";
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Notification endpoint not found\", \"status\": \"error\"}";
                exchange.sendResponseHeaders(404, response.getBytes().length);
            } else {
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
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
            
            if ("GET".equals(method) && pathParts.length >= 4) {
                String section = pathParts[2];
                String identifier = pathParts[3];
                
                if ("user".equals(section)) {
                    // GET /notifications/user/{userId}
                    response = "{\"message\": \"Get notifications for user " + identifier + "\", \"status\": \"success\"}";
                } else if ("type".equals(section)) {
                    // GET /notifications/type/{type}
                    response = "{\"message\": \"Get notifications of type " + identifier + "\", \"status\": \"success\"}";
                }
            } else if ("PUT".equals(method) && pathParts.length == 4 && "read".equals(pathParts[3])) {
                // PUT /notifications/{id}/read
                String notificationId = pathParts[2];
                response = "{\"message\": \"Mark notification " + notificationId + " as read\", \"status\": \"success\"}";
            }
            
            if (response.isEmpty()) {
                response = "{\"message\": \"Notification endpoint not found\", \"status\": \"error\"}";
                exchange.sendResponseHeaders(404, response.getBytes().length);
            } else {
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
    
    private static void initializeSampleData(NotificationController controller) {
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
