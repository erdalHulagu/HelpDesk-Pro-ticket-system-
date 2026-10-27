
package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> findAllByRecipientId(Long recipientId);

    long countUnreadByRecipientId(Long recipientId);

    boolean markAsRead(Long notificationId, Long recipientId);

    int markAllAsRead(Long recipientId);
}
