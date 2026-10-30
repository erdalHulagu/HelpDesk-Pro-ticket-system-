
package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.repository.NotificationRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            TicketRepository ticketRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Notification createNotification(
            Long recipientId,
            Long ticketId,
            Long actorId,
            String type,
            String title,
            String message) {

        User recipient = userRepository.findById(recipientId);
        if (recipient == null) {
            throw new IllegalArgumentException("Bildirim alıcısı bulunamadı.");
        }

        User actor = null;
        if (actorId != null) {
            actor = userRepository.findById(actorId);
            if (actor == null) {
                throw new IllegalArgumentException("İşlemi yapan kullanıcı bulunamadı.");
            }
        }

        Ticket ticket = null;
        if (ticketId != null) {
            ticket = ticketRepository.findById(ticketId);
            if (ticket == null) {
                throw new IllegalArgumentException("Ticket bulunamadı.");
            }
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setTicket(ticket);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getMyNotifications(User currentUser) {
        validateUser(currentUser);

        return notificationRepository.findAllByRecipientId(
                currentUser.getId()
        );
    }

    @Override
    public long getUnreadCount(User currentUser) {
        validateUser(currentUser);

        return notificationRepository.countUnreadByRecipientId(
                currentUser.getId()
        );
    }
    @Override
    public boolean markAsRead(Long notificationId, User currentUser) {
        validateUser(currentUser);

        // Kullanıcı yalnızca kendi bildirimini değiştirebilir.
        // Admin ve IT Manager da başkasının bildirimini değiştiremez.
        return notificationRepository.markAsRead(
                notificationId,
                currentUser.getId()
        );
    }
    @Override
    public int markAllAsRead(User currentUser) {
        validateUser(currentUser);

        return notificationRepository.markAllAsRead(
                currentUser.getId()
        );
    }

    private void validateUser(User user) {
        if (user == null || user.getId() == null || user.getRole() == null) {
            throw new IllegalArgumentException("Geçersiz kullanıcı.");
        }
    }
    
 // Delete one notification from the user's notification list.
    @Override
    public boolean deleteNotification(Long notificationId, User currentUser) {
        validateUser(currentUser);

        if (notificationId == null) {
            throw new IllegalArgumentException("Geçersiz bildirim ID.");
        }

        // Only ADMIN can permanently delete notifications.
        if (currentUser.getRole() == Role.ADMIN) {
            return notificationRepository.permanentlyDelete(
                    notificationId,
                    currentUser.getId()
            );
        }

        // Other users can only hide their own notifications.
        return notificationRepository.hideNotification(
                notificationId,
                currentUser.getId()
        );
    }

    // Clear all notifications from the user's notification list.
    @Override
    public int deleteAllNotifications(User currentUser) {
        validateUser(currentUser);

        // Only ADMIN can permanently delete notifications.
        if (currentUser.getRole() == Role.ADMIN) {
            return notificationRepository.permanentlyDeleteAll(
                    currentUser.getId()
            );
        }

        // Other users can only hide their own notifications.
        return notificationRepository.hideAllNotifications(
                currentUser.getId()
        );
    }
}
