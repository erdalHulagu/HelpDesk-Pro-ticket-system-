package com.erdal.helpdeskpro.service;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.User;

public interface AnnouncementReadService {

    void markAsRead(Long announcementId, User currentUser);

    boolean isRead(Long announcementId, User currentUser);

    List<Long> getReadAnnouncementIds(User currentUser);

	LocalDateTime getReadAt(Long announcementId, User currentUser);

	void markAllAsRead(User currentUser);
}
