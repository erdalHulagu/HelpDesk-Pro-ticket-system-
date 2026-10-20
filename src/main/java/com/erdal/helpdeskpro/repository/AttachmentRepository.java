package com.erdal.helpdeskpro.repository;


import java.util.List;

import com.erdal.helpdeskpro.domain.Attachment;

public interface AttachmentRepository {
    
    Attachment saveUserAttachment(Attachment attachment);

    Attachment findById(Long id);

    List<Attachment> findByTicketId(Long ticketId);

    void deleteById(Long id);

	Attachment saveTicketAttachment(Attachment attachment);

	
}

