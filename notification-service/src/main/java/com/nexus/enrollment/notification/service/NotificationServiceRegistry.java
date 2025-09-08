package com.nexus.enrollment.notification.service;

import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;

/**
 * Service registry for Notification Service operations
 * Provides convenient methods for other microservices to send notifications
 */
public class NotificationServiceRegistry {
    private static final ServiceClient serviceClient = new ServiceClient();
    
    /**
     * Send notification
     */
    public static ServiceResponse<String> sendNotification(Object notification) {
        return serviceClient.post("notification", "/notifications", notification, String.class);
    }
    
    /**
     * Get user notifications
     */
    public static ServiceResponse<String> getUserNotifications(Long userId) {
        return serviceClient.get("notification", "/notifications/user/" + userId, String.class);
    }
    
    /**
     * Get notifications by type
     */
    public static ServiceResponse<String> getNotificationsByType(String type) {
        return serviceClient.get("notification", "/notifications/type/" + type, String.class);
    }
    
    /**
     * Mark notification as read
     */
    public static ServiceResponse<String> markAsRead(Long notificationId) {
        return serviceClient.put("notification", "/notifications/" + notificationId + "/read", null, String.class);
    }
    
    /**
     * Subscribe to notification type
     */
    public static ServiceResponse<String> subscribeToNotificationType(Object subscription) {
        return serviceClient.post("notification", "/notifications/subscribe", subscription, String.class);
    }
    
    /**
     * Send enrollment confirmation notification (convenience method)
     */
    public static ServiceResponse<String> sendEnrollmentConfirmation(Long studentId, Long courseId) {
        EnrollmentNotificationRequest request = new EnrollmentNotificationRequest(studentId, courseId, "ENROLLMENT_CONFIRMATION");
        return sendNotification(request);
    }
    
    /**
     * Send course drop notification (convenience method)
     */
    public static ServiceResponse<String> sendDropConfirmation(Long studentId, Long courseId) {
        EnrollmentNotificationRequest request = new EnrollmentNotificationRequest(studentId, courseId, "DROP_CONFIRMATION");
        return sendNotification(request);
    }
    
    /**
     * Send waitlist notification (convenience method)
     */
    public static ServiceResponse<String> sendWaitlistNotification(Long studentId, Long courseId) {
        EnrollmentNotificationRequest request = new EnrollmentNotificationRequest(studentId, courseId, "WAITLIST_NOTIFICATION");
        return sendNotification(request);
    }
    
    // Inner class for enrollment notification requests
    public static class EnrollmentNotificationRequest {
        private final Long studentId;
        private final Long courseId;
        private final String notificationType;
        
        public EnrollmentNotificationRequest(Long studentId, Long courseId, String notificationType) {
            this.studentId = studentId;
            this.courseId = courseId;
            this.notificationType = notificationType;
        }
        
        public Long getStudentId() { return studentId; }
        public Long getCourseId() { return courseId; }
        public String getNotificationType() { return notificationType; }
    }
}
