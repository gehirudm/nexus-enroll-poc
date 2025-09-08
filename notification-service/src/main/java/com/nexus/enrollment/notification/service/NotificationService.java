package com.nexus.enrollment.notification.service;

import com.nexus.enrollment.common.model.Notification;
import com.nexus.enrollment.common.enums.NotificationType;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.notification.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<Long, Set<NotificationType>> userSubscriptions = new HashMap<>();
    
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        // In a real implementation, this would also send the notification via email/SMS/push
        System.out.println("Notification sent to user " + notification.getUserId() + ": " + notification.getMessage());
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByType(type);
    }
    
    public void subscribeToNotifications(Long userId, NotificationType type) {
        userSubscriptions.computeIfAbsent(userId, k -> new HashSet<>()).add(type);
        System.out.println("User " + userId + " subscribed to " + type + " notifications");
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }
    
    public Notification createNotification(Long userId, NotificationType type, String message) {
        Notification notification = new Notification(userId, type, message);
        return notificationRepository.save(notification);
    }
    
    public boolean isUserSubscribed(Long userId, NotificationType type) {
        Set<NotificationType> subscriptions = userSubscriptions.get(userId);
        return subscriptions != null && subscriptions.contains(type);
    }
}
