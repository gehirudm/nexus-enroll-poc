package notification_service.controller;

import common.model.Notification;
import common.enums.NotificationType;
import common.util.ResponseBuilder;
import notification_service.service.NotificationService;
import java.util.List;

public class NotificationController {
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public ResponseBuilder.Response sendNotification(Notification notification) {
        try {
            notificationService.sendNotification(notification);
            return ResponseBuilder.success("Notification sent successfully", notification);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response createAndSendNotification(Long userId, NotificationType type, String message) {
        try {
            Notification notification = notificationService.createNotification(userId, type, message);
            notificationService.sendNotification(notification);
            return ResponseBuilder.success("Notification created and sent successfully", notification);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getUserNotifications(Long userId) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return ResponseBuilder.success("User notifications retrieved successfully", notifications);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getNotificationsByType(NotificationType type) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByType(type);
            return ResponseBuilder.success("Notifications retrieved successfully", notifications);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getUnreadNotifications(Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseBuilder.success("Unread notifications retrieved successfully", notifications);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response markAsRead(Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseBuilder.success("Notification marked as read", null);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response subscribeToNotifications(Long userId, NotificationType type) {
        try {
            notificationService.subscribeToNotifications(userId, type);
            return ResponseBuilder.success("Successfully subscribed to notifications", null);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
}
