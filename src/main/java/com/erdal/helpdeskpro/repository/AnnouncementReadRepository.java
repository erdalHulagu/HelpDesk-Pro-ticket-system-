
package com.erdal.helpdeskpro.repository;

import java.util.List;

public interface AnnouncementReadRepository {

    void markAsRead(Long announcementId, Long userId);

    boolean isRead(Long announcementId, Long userId);

    List<Long> getReadAnnouncementIds(Long userId);
}
