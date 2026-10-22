
package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Attachment;

public interface AttachmentRepository {
	// ----------------------------
	// Save attachments
	// ----------------------------
	Attachment saveUserAttachment(Attachment attachment);

	Attachment saveTicketAttachment(Attachment attachment);

	Attachment saveCommentAttachment(Attachment attachment);

	// ----------------------------
	// Find attachments
	// ----------------------------
	Attachment findById(Long id);

	Attachment findUserAttachment(Long userId);
	
	Attachment findProfilePhoto(Long userId);

	List<Attachment> findByTicketId(Long ticketId);

	List<Attachment> findByCommentId(Long commentId);

	List<Attachment> findByTicketIdIncludingComments(Long ticketId);

	// ----------------------------
	// Delete attachments
	// ----------------------------
	void deleteById(Long id);

	
}
