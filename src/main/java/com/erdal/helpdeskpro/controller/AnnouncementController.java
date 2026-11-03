
package com.erdal.helpdeskpro.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.service.AnnouncementService;

public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    public Announcement createAnnouncement(
            String title,
            String message,
            Role targetRole,
            LocalDateTime expiresAt,
            boolean pinned,
            User user) {

        return announcementService.createAnnouncement(
                title, message, targetRole, expiresAt, pinned, user);
    }

    public Announcement updateAnnouncement(
            Long announcementId,
            String title,
            String message,
            Role targetRole,
            LocalDateTime expiresAt,
            boolean pinned,
            boolean active,
            User user) {

        return announcementService.updateAnnouncement(
                announcementId,
                title,
                message,
                targetRole,
                expiresAt,
                pinned,
                active,
                user);
    }

    public List<Announcement> getAllAnnouncements(User user) {
        return announcementService.getAllAnnouncements(user);
    }

    public List<Announcement> getMyAnnouncements(User user) {
        return announcementService.getMyAnnouncements(user);
    }

    public boolean deleteAnnouncement(Long announcementId, User user) {
        return announcementService.deleteAnnouncement(announcementId, user);
    }
}
