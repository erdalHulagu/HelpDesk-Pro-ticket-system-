
package com.erdal.helpdeskpro.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.AnnouncementRepository;
import com.erdal.helpdeskpro.service.AnnouncementService;

public class AnnouncementServiceImpl implements AnnouncementService {

	private final AnnouncementRepository announcementRepository;

	public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
		this.announcementRepository = announcementRepository;
	}

	@Override
	public Announcement createAnnouncement(String title, String message, Role targetRole, LocalDateTime expiresAt,
			boolean pinned, User currentUser) {

		validateAdmin(currentUser);
		validateAnnouncementData(title, message, expiresAt);

		Announcement announcement = new Announcement();

		announcement.setTitle(title.trim());
		announcement.setMessage(message.trim());
		announcement.setTargetRole(targetRole);
		announcement.setCreatedBy(currentUser);
		announcement.setCreatedAt(LocalDateTime.now());
		announcement.setExpiresAt(expiresAt);
		announcement.setPinned(pinned);
		announcement.setActive(true);

		return announcementRepository.save(announcement);
	}

	@Override
	public Announcement updateAnnouncement(Long announcementId, String title, String message, Role targetRole,
			LocalDateTime expiresAt, boolean pinned, boolean active, User currentUser) {

		validateAdmin(currentUser);
		validateAnnouncementData(title, message, expiresAt);

		if (announcementId == null) {
			throw new IllegalArgumentException("Announcement ID cannot be null.");
		}

		Announcement announcement = announcementRepository.findById(announcementId)
				.orElseThrow(() -> new IllegalArgumentException("Announcement not found."));

		announcement.setTitle(title.trim());
		announcement.setMessage(message.trim());
		announcement.setTargetRole(targetRole);
		announcement.setExpiresAt(expiresAt);
		announcement.setPinned(pinned);
		announcement.setActive(active);
		announcement.setUpdatedAt(LocalDateTime.now());

		return announcementRepository.save(announcement);
	}

	@Override
	public List<Announcement> getAllAnnouncements(User currentUser) {

		validateAdmin(currentUser);

		return announcementRepository.findAllAnnouncements();
	}

	@Override
	public List<Announcement> getMyAnnouncements(User currentUser) {

		validateUser(currentUser);

		return announcementRepository.findActiveAnnouncementsByRole(currentUser.getRole());
	}

	@Override
	public boolean deleteAnnouncement(Long announcementId, User currentUser) {

		validateAdmin(currentUser);

		if (announcementId == null) {
			throw new IllegalArgumentException("Announcement ID cannot be null.");
		}

		return announcementRepository.deleteById(announcementId);
	}

	private void validateUser(User user) {

		if (user == null || user.getId() == null || user.getRole() == null) {
			throw new IllegalArgumentException("Invalid user.");
		}
	}

	private void validateAdmin(User user) {

		validateUser(user);

		if (user.getRole() != Role.ADMIN) {
			throw new SecurityException("Only administrators can manage announcements.");
		}
	}

	private void validateAnnouncementData(String title, String message, LocalDateTime expiresAt) {

		if (title == null || title.trim().isEmpty()) {
			throw new IllegalArgumentException("Announcement title is required.");
		}

		if (title.trim().length() > 150) {
			throw new IllegalArgumentException("Announcement title cannot exceed 150 characters.");
		}

		if (message == null || message.trim().isEmpty()) {
			throw new IllegalArgumentException("Announcement message is required.");
		}

		if (message.trim().length() > 2000) {
			throw new IllegalArgumentException("Announcement message cannot exceed 2000 characters.");
		}

		if (expiresAt != null && !expiresAt.isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("Expiration date must be in the future.");
		}
	}
}
