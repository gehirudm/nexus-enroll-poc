package notification_service.repository;

import common.model.Notification;
import common.repository.CrudRepository;
import common.enums.NotificationType;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByType(NotificationType type);
    List<Notification> findUnreadByUserId(Long userId);
}
