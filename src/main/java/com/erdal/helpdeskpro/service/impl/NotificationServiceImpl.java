
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

        Role role = currentUser.getRole();

        if (role == Role.ADMIN || role == Role.IT_MANAGER) {
            return notificationRepository.findAllNotifications();
        }

        return notificationRepository.findAllByRecipientId(currentUser.getId());
    }

    @Override
    public long getUnreadCount(User currentUser) {
        validateUser(currentUser);

        Role role = currentUser.getRole();

        if (role == Role.ADMIN || role == Role.IT_MANAGER) {
            return notificationRepository.countAllUnread();
        }

        return notificationRepository.countUnreadByRecipientId(currentUser.getId());
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

        // Yalnızca giriş yapan kullanıcının bildirimleri güncellenir.
        return notificationRepository.markAllAsRead(currentUser.getId());
    }

    private void validateUser(User user) {
        if (user == null || user.getId() == null || user.getRole() == null) {
            throw new IllegalArgumentException("Geçersiz kullanıcı.");
        }
    }
}
