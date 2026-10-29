
package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> findAllByRecipientId(Long recipientId);

    List<Notification> findAllNotifications();

    long countUnreadByRecipientId(Long recipientId);

    long countAllUnread();

    boolean markAsRead(Long notificationId, Long recipientId);

    int markAllAsRead(Long recipientId);
}
