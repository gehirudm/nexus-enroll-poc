package com.nexus.enrollment.notification.repository;

import com.nexus.enrollment.common.model.Notification;
import com.nexus.enrollment.common.repository.CrudRepository;
import com.nexus.enrollment.common.enums.NotificationType;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByType(NotificationType type);
    List<Notification> findUnreadByUserId(Long userId);
}
