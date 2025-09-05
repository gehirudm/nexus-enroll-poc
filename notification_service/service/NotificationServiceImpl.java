package notification_service.service;

import common.model.Notification;
import common.enums.NotificationType;
import common.exceptions.NotFoundException;
import notification_service.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<Long, Set<NotificationType>> userSubscriptions = new HashMap<>();
    
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        // In a real implementation, this would also send the notification via email/SMS/push
        System.out.println("Notification sent to user " + notification.getUserId() + ": " + notification.getMessage());
    }
    
    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    @Override
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByType(type);
    }
    
    @Override
    public void subscribeToNotifications(Long userId, NotificationType type) {
        userSubscriptions.computeIfAbsent(userId, k -> new HashSet<>()).add(type);
        System.out.println("User " + userId + " subscribed to " + type + " notifications");
    }
    
    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }
    
    @Override
    public Notification createNotification(Long userId, NotificationType type, String message) {
        Notification notification = new Notification(userId, type, message);
        return notificationRepository.save(notification);
    }
    
    public boolean isUserSubscribed(Long userId, NotificationType type) {
        Set<NotificationType> subscriptions = userSubscriptions.get(userId);
        return subscriptions != null && subscriptions.contains(type);
    }
}
