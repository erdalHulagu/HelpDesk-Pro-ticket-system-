
package com.erdal.helpdeskpro.service;

import java.io.InputStream;
import java.util.List;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.User;

public interface AttachmentService {

	Attachment uploadForTicket(Long ticketId, String fileName, String contentType, InputStream inputStream, User currentUser);
	
	Attachment uploadForUser(String fileName, String contentType, InputStream inputStream, User currentUser);

	List<Attachment> getAttachmentsByTicket(Long ticketId, User currentUser);

	void deleteAttachment(Long attachmentId, User currentUser);

	Attachment getAttachmentForDownload(Long attachmentId, User currentUser);
}
