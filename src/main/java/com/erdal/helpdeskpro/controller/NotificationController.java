
package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.service.NotificationService;

public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public List<Notification> getMyNotifications(User user) {
        return notificationService.getMyNotifications(user);
    }

    public long getUnreadCount(User user) {
        return notificationService.getUnreadCount(user);
    }

    public boolean markAsRead(Long notificationId, User user) {
        return notificationService.markAsRead(notificationId, user);
    }

    public int markAllAsRead(User user) {
        return notificationService.markAllAsRead(user);
    }
    public boolean deleteNotification(Long notificationId, User user) {
        return notificationService.deleteNotification(notificationId, user);
    }

    public int deleteAllNotifications(User user) {
        return notificationService.deleteAllNotifications(user);
    }
}
