
package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.domain.User;

public interface NotificationService {

    Notification createNotification(
            Long recipientId,
            Long ticketId,
            Long actorId,
            String type,
            String title,
            String message
    );

    List<Notification> getMyNotifications(User currentUser);

    long getUnreadCount(User currentUser);

    boolean markAsRead(Long notificationId, User currentUser);

    int markAllAsRead(User currentUser);
    
    boolean deleteNotification(Long notificationId, User currentUser);

    int deleteAllNotifications(User currentUser);
}
