
package com.erdal.helpdeskpro.mapper;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.AnnouncementDTO;

public class AnnouncementMapper {

    private AnnouncementMapper() {
        // Utility class
    }

    public static AnnouncementDTO toDTO(Announcement announcement) {

        if (announcement == null) {
            return null;
        }

        AnnouncementDTO dto = new AnnouncementDTO();

        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setMessage(announcement.getMessage());
        dto.setTargetRole(announcement.getTargetRole());

        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());
        dto.setExpiresAt(announcement.getExpiresAt());

        dto.setPinned(announcement.isPinned());
        dto.setActive(announcement.isActive());

        User createdBy = announcement.getCreatedBy();

        if (createdBy != null) {
            dto.setCreatedById(createdBy.getId());
            dto.setCreatedByUsername(createdBy.getUsername());
        }

        return dto;
    }
}
