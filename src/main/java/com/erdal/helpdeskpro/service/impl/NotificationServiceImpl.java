
package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
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

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bildirim alıcısı bulunamadı."));

        User actor = null;
        if (actorId != null) {
            actor = userRepository.findById(actorId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("İşlemi yapan kullanıcı bulunamadı."));
        }

        Ticket ticket = null;
        if (ticketId != null) {
            ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Ticket bulunamadı."));
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
    public List<Notification> getMyNotifications(Long recipientId) {
        return notificationRepository.findAllByRecipientId(recipientId);
    }

    @Override
    public long getUnreadCount(Long recipientId) {
        return notificationRepository.countUnreadByRecipientId(recipientId);
    }

    @Override
    public boolean markAsRead(Long notificationId, Long recipientId) {
        return notificationRepository.markAsRead(notificationId, recipientId);
    }

    @Override
    public int markAllAsRead(Long recipientId) {
        return notificationRepository.markAllAsRead(recipientId);
    }
}
