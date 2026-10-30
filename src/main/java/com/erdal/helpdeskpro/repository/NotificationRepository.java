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

    // Hide one notification from its owner.
    boolean hideNotification(Long notificationId, Long recipientId);

    // Hide all notifications belonging to the user.
    int hideAllNotifications(Long recipientId);

    // Permanently delete one notification.
    boolean permanentlyDelete(Long notificationId, Long recipientId);

    // Permanently delete all notifications belonging to the user.
    int permanentlyDeleteAll(Long recipientId);
}