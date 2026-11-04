
package com.erdal.helpdeskpro.service;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.domain.User;

public interface AnnouncementService {

	// Create a new announcement.
	Announcement createAnnouncement(String title, String message, Role targetRole, LocalDateTime expiresAt,
			boolean pinned, User currentUser);

	// Update an existing announcement.
	Announcement updateAnnouncement(Long announcementId, String title, String message, Role targetRole,
			LocalDateTime expiresAt, boolean pinned, boolean active, User currentUser);

	// Get all announcements (Admin).
	List<Announcement> getAllAnnouncements(User currentUser);

	// Get active announcements for the current user's role.
	List<Announcement> getMyAnnouncements(User currentUser);

	// Delete an announcement.
	boolean deleteAnnouncement(Long announcementId, User currentUser);
}
