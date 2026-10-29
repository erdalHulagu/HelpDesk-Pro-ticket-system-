package com.erdal.helpdeskpro.service;
import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;

public interface NotificationService {

    Notification createNotification(
            Long recipientId,
            Long ticketId,
            Long actorId,
            String type,
            String title,
            String message
    );

    List<Notification> getMyNotifications(Long recipientId);

    long getUnreadCount(Long recipientId);

    boolean markAsRead(Long notificationId, Long recipientId);

    int markAllAsRead(Long recipientId);
}
