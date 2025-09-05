package com.nexus.enrollment.notification;

import com.nexus.enrollment.common.model.Notification;
import com.nexus.enrollment.common.enums.NotificationType;
import com.nexus.enrollment.notification.repository.NotificationRepository;
import com.nexus.enrollment.notification.repository.InMemoryNotificationRepository;
import com.nexus.enrollment.notification.service.NotificationService;
import com.nexus.enrollment.notification.service.NotificationServiceImpl;
import com.nexus.enrollment.notification.controller.NotificationController;

public class NotificationServiceApplication {
    
    public static void main(String[] args) {
        // Initialize repositories
        NotificationRepository notificationRepo = new InMemoryNotificationRepository();
        
        // Initialize services
        NotificationService notificationService = new NotificationServiceImpl(notificationRepo);
        
        // Initialize controller
        NotificationController controller = new NotificationController(notificationService);
        
        // Initialize with sample data
        initializeSampleData(controller);
        
        System.out.println("Notification Service Application started successfully!");
        
        // Demo usage
        demonstrateService(controller);
    }
    
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
    
    private static void demonstrateService(NotificationController controller) {
        System.out.println("\n=== Notification Service Demo ===");
        
        // Get user notifications
        var userNotifications = controller.getUserNotifications(1L);
        System.out.println("User 1 notifications: " + userNotifications.isSuccess());
        
        // Get unread notifications
        var unreadNotifications = controller.getUnreadNotifications(1L);
        System.out.println("User 1 unread notifications: " + unreadNotifications.isSuccess());
        
        // Mark notification as read
        var markReadResponse = controller.markAsRead(1L);
        System.out.println("Mark notification as read: " + markReadResponse.isSuccess());
        
        // Get notifications by type
        var gradeNotifications = controller.getNotificationsByType(NotificationType.GRADE_SUBMITTED);
        System.out.println("Grade notifications: " + gradeNotifications.isSuccess());
        
        System.out.println("Demo completed!");
    }
}
