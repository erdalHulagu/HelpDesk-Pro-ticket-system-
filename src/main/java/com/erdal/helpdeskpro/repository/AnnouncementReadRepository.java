
package com.erdal.helpdeskpro.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementReadRepository {

    void markAsRead(Long announcementId, Long userId);

    boolean isRead(Long announcementId, Long userId);

    List<Long> getReadAnnouncementIds(Long userId);

	LocalDateTime getReadAt(Long announcementId, Long userId);

	void markAllAsRead(List<Long> announcementIds, Long userId);
}
