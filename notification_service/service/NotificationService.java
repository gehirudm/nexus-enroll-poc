package notification_service.service;

import common.model.Notification;
import common.enums.NotificationType;
import java.util.List;

public interface NotificationService {
    void sendNotification(Notification notification);
    List<Notification> getUserNotifications(Long userId);
    List<Notification> getNotificationsByType(NotificationType type);
    void subscribeToNotifications(Long userId, NotificationType type);
    void markAsRead(Long notificationId);
    List<Notification> getUnreadNotifications(Long userId);
    Notification createNotification(Long userId, NotificationType type, String message);
}
