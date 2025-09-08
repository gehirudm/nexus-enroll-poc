package com.nexus.enrollment.notification.handler;

import com.nexus.enrollment.common.model.Notification;
import com.nexus.enrollment.common.enums.NotificationType;
import com.nexus.enrollment.notification.service.NotificationService;
import io.javalin.http.Context;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List;

public class NotificationHandler {
    private final NotificationService notificationService;
    private final Gson gson = new Gson();
    
    public NotificationHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * POST /notifications - Send a notification
     */
    public void sendNotification(Context ctx) {
        try {
            Notification notification = ctx.bodyAsClass(Notification.class);
            notificationService.sendNotification(notification);
            ctx.json(createSuccessResponse("Notification sent successfully", notification));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /notifications/create - Create and send a notification
     */
    public void createAndSendNotification(Context ctx) {
        try {
            JsonObject requestBody = gson.fromJson(ctx.body(), JsonObject.class);
            Long userId = requestBody.get("userId").getAsLong();
            String typeStr = requestBody.get("type").getAsString();
            String message = requestBody.get("message").getAsString();
            
            NotificationType type = NotificationType.valueOf(typeStr);
            Notification notification = notificationService.createNotification(userId, type, message);
            notificationService.sendNotification(notification);
            ctx.json(createSuccessResponse("Notification created and sent successfully", notification));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /notifications/user/{userId} - Get notifications for a user
     */
    public void getUserNotifications(Context ctx) {
        try {
            Long userId = Long.valueOf(ctx.pathParam("userId"));
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            ctx.json(createSuccessResponse("User notifications retrieved successfully", notifications));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /notifications/type/{type} - Get notifications by type
     */
    public void getNotificationsByType(Context ctx) {
        try {
            String typeStr = ctx.pathParam("type");
            NotificationType type = NotificationType.valueOf(typeStr);
            List<Notification> notifications = notificationService.getNotificationsByType(type);
            ctx.json(createSuccessResponse("Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * GET /notifications/user/{userId}/unread - Get unread notifications for a user
     */
    public void getUnreadNotifications(Context ctx) {
        try {
            Long userId = Long.valueOf(ctx.pathParam("userId"));
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            ctx.json(createSuccessResponse("Unread notifications retrieved successfully", notifications));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * PUT /notifications/{notificationId}/read - Mark notification as read
     */
    public void markAsRead(Context ctx) {
        try {
            Long notificationId = Long.valueOf(ctx.pathParam("notificationId"));
            notificationService.markAsRead(notificationId);
            ctx.json(createSuccessResponse("Notification marked as read", null));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * POST /notifications/subscribe - Subscribe to notification type
     */
    public void subscribeToNotifications(Context ctx) {
        try {
            JsonObject requestBody = gson.fromJson(ctx.body(), JsonObject.class);
            Long userId = requestBody.get("userId").getAsLong();
            String typeStr = requestBody.get("type").getAsString();
            
            NotificationType type = NotificationType.valueOf(typeStr);
            notificationService.subscribeToNotifications(userId, type);
            ctx.json(createSuccessResponse("Successfully subscribed to notifications", null));
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse(e.getMessage()));
        }
    }
    
    // Helper methods for response formatting
    private Object createSuccessResponse(String message, Object data) {
        return new ResponseWrapper("success", message, data);
    }
    
    private Object createErrorResponse(String message) {
        return new ResponseWrapper("error", message, null);
    }
    
    // Response wrapper class
    public static class ResponseWrapper {
        public final String status;
        public final String message;
        public final Object data;
        
        public ResponseWrapper(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }
}
