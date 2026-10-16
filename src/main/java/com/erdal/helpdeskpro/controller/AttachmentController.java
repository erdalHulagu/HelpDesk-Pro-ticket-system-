
package com.erdal.helpdeskpro.controller;

import java.io.InputStream;
import java.util.List;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.service.AttachmentService;

public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    public Attachment upload(
            Long ticketId,
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        return attachmentService.upload(
                ticketId,
                fileName,
                contentType,
                inputStream,
                currentUser
        );
    }

    public List<Attachment> findByTicket(
            Long ticketId,
            User currentUser) {

        return attachmentService.getAttachmentsByTicket(
                ticketId,
                currentUser
        );
    }

    public Attachment download(
            Long attachmentId,
            User currentUser) {

        return attachmentService.getAttachmentForDownload(
                attachmentId,
                currentUser
        );
    }

    public void delete(
            Long attachmentId,
            User currentUser) {

        attachmentService.deleteAttachment(
                attachmentId,
                currentUser
        );
    }
}
