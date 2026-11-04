
package com.erdal.helpdeskpro.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.AnnouncementReadRepository;
import com.erdal.helpdeskpro.repository.AnnouncementRepository;
import com.erdal.helpdeskpro.service.AnnouncementReadService;

public class AnnouncementReadServiceImpl
        implements AnnouncementReadService {

    private final AnnouncementReadRepository announcementReadRepository;
    private final AnnouncementRepository announcementRepository;

    public AnnouncementReadServiceImpl(
            AnnouncementReadRepository announcementReadRepository,
            AnnouncementRepository announcementRepository) {

        this.announcementReadRepository = announcementReadRepository;
        this.announcementRepository = announcementRepository;
    }

    @Override
    public void markAsRead(Long announcementId, User currentUser) {

        validateUser(currentUser);
        validateAnnouncementId(announcementId);
        validateAnnouncementVisibility(announcementId, currentUser);

        announcementReadRepository.markAsRead(
                announcementId,
                currentUser.getId()
        );
    }

    @Override
    public boolean isRead(Long announcementId, User currentUser) {

        validateUser(currentUser);
        validateAnnouncementId(announcementId);

        return announcementReadRepository.isRead(
                announcementId,
                currentUser.getId()
        );
    }

    @Override
    public List<Long> getReadAnnouncementIds(User currentUser) {

        validateUser(currentUser);

        return announcementReadRepository.getReadAnnouncementIds(
                currentUser.getId()
        );
    }

    private void validateAnnouncementVisibility(
            Long announcementId,
            User currentUser) {

        List<Announcement> announcements =
                announcementRepository.findActiveAnnouncementsByRole(
                        currentUser.getRole()
                );

        boolean visible = announcements.stream()
                .anyMatch(a -> announcementId.equals(a.getId()));

        if (!visible) {
            throw new IllegalArgumentException(
                    "Announcement not found or not available to this user."
            );
        }
    }

    private void validateAnnouncementId(Long announcementId) {

        if (announcementId == null) {
            throw new IllegalArgumentException(
                    "Announcement ID cannot be null."
            );
        }
    }

    private void validateUser(User user) {

        if (user == null || user.getId() == null || user.getRole() == null) {
            throw new IllegalArgumentException("Invalid user.");
        }
    }
    
    @Override
    public LocalDateTime getReadAt(Long announcementId, User currentUser) {

        validateUser(currentUser);
        validateAnnouncementId(announcementId);

        return announcementReadRepository.getReadAt(
                announcementId,
                currentUser.getId()
        );
    }
}
