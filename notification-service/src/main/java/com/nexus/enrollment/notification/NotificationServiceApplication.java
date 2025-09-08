package com.nexus.enrollment.notification;

import com.nexus.enrollment.common.enums.NotificationType;
import com.nexus.enrollment.notification.repository.NotificationRepository;
import com.nexus.enrollment.notification.repository.InMemoryNotificationRepository;
import com.nexus.enrollment.notification.service.NotificationService;
import com.nexus.enrollment.notification.handler.NotificationHandler;
import io.javalin.Javalin;

public class NotificationServiceApplication {

    private static final int PORT = 8085;

    public static void main(String[] args) {
        // Initialize repositories
        NotificationRepository notificationRepo = new InMemoryNotificationRepository();

        // Initialize services
        NotificationService notificationService = new NotificationService(notificationRepo);

        // Initialize handler
        NotificationHandler handler = new NotificationHandler(notificationService);

        // Initialize with sample data
        initializeSampleData(notificationService);

        // Start Javalin server
        startJavalinServer(handler);
    }

    private static void startJavalinServer(NotificationHandler handler) {
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        }).start(PORT);

        // Notification Service Endpoints
        app.post("/notifications", handler::sendNotification);
        app.post("/notifications/create", handler::createAndSendNotification);
        app.get("/notifications/user/{userId}", handler::getUserNotifications);
        app.get("/notifications/type/{type}", handler::getNotificationsByType);
        app.get("/notifications/user/{userId}/unread", handler::getUnreadNotifications);
        app.put("/notifications/{notificationId}/read", handler::markAsRead);
        app.post("/notifications/subscribe", handler::subscribeToNotifications);

        System.out.println("Notification Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  POST /notifications - Send notification");
        System.out.println("  POST /notifications/create - Create and send notification");
        System.out.println("  GET /notifications/user/{userId} - Get user notifications");
        System.out.println("  GET /notifications/type/{type} - Get notifications by type");
        System.out.println("  GET /notifications/user/{userId}/unread - Get unread notifications");
        System.out.println("  PUT /notifications/{notificationId}/read - Mark notification as read");
        System.out.println("  POST /notifications/subscribe - Subscribe to notification type");
    }

    private static void initializeSampleData(NotificationService notificationService) {
        // Create some sample notifications
        notificationService.sendNotification(notificationService.createNotification(1L, NotificationType.ENROLLMENT_CONFIRMATION, 
                "You have been successfully enrolled in CS101"));
        notificationService.sendNotification(notificationService.createNotification(2L, NotificationType.GRADE_SUBMITTED, 
                "Your grade for MATH201 has been submitted"));
        notificationService.sendNotification(notificationService.createNotification(1L, NotificationType.COURSE_FULL, 
                "CS201 is now full. You have been added to the waitlist"));
        
        // Subscribe users to notification types
        notificationService.subscribeToNotifications(1L, NotificationType.ENROLLMENT_CONFIRMATION);
        notificationService.subscribeToNotifications(1L, NotificationType.WAITLIST_AVAILABLE);
        notificationService.subscribeToNotifications(2L, NotificationType.GRADE_SUBMITTED);
        
        System.out.println("Sample notification data initialized");
    }
}
