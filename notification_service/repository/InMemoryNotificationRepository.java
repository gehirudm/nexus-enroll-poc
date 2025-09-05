package notification_service.repository;

import common.model.Notification;
import common.enums.NotificationType;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<Long, Notification> notifications = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(nextId++);
        }
        notifications.put(notification.getId(), notification);
        return notification;
    }
    
    @Override
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notifications.get(id));
    }
    
    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }
    
    @Override
    public void deleteById(Long id) {
        notifications.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return notifications.containsKey(id);
    }
    
    @Override
    public List<Notification> findByUserId(Long userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Notification> findByType(NotificationType type) {
        return notifications.values().stream()
                .filter(notification -> notification.getType().equals(type))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.isRead())
                .collect(Collectors.toList());
    }
}
