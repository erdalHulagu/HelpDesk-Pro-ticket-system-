package com.erdal.helpdeskpro.dtos;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.domain.Notification;

public class NotificationDTO {

    private Long id;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.read = notification.isRead();
        this.createdAt = notification.getCreatedAt();
        this.ticketId = notification.getTicket() != null
                ? notification.getTicket().getId()
                : null;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    public Long getTicketId() {
        return ticketId;
    }

    public String getMessage() {
        return message;
    }
    
    private Long ticketId;

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}