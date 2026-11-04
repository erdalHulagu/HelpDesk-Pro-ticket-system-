
package com.erdal.helpdeskpro.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.service.AnnouncementReadService;

public class AnnouncementReadController {

    private final AnnouncementReadService announcementReadService;

    public AnnouncementReadController(
            AnnouncementReadService announcementReadService) {
        this.announcementReadService = announcementReadService;
    }

    /**
     * Marks an announcement as read by the current user.
     */
    public void markAsRead(Long announcementId, User currentUser) {
        announcementReadService.markAsRead(announcementId, currentUser);
    }

    /**
     * Checks whether the current user has read an announcement.
     */
    public boolean isRead(Long announcementId, User currentUser) {
        return announcementReadService.isRead(announcementId, currentUser);
    }

    /**
     * Returns IDs of announcements read by the current user.
     */
    public List<Long> getReadAnnouncementIds(User currentUser) {
        return announcementReadService.getReadAnnouncementIds(currentUser);
    }

    /**
     * Returns the date and time when the user read an announcement.
     */
    public LocalDateTime getReadAt(Long announcementId, User currentUser) {
        return announcementReadService.getReadAt(announcementId, currentUser);
    }
}
