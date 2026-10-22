
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

    // Ticket'a dosya ekle
    public Attachment uploadForTicket(
            Long ticketId,
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        return attachmentService.uploadForTicket(
                ticketId,
                fileName,
                contentType,
                inputStream,
                currentUser
        );
    }

    // Yoruma dosya ekle
    public Attachment uploadForComment(
            Long commentId,
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        return attachmentService.uploadForComment(
                commentId,
                fileName,
                contentType,
                inputStream,
                currentUser
        );
    }

    // Kullanıcıya dosya ekle
    public Attachment uploadForUser(
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        return attachmentService.uploadForUser(
                fileName,
                contentType,
                inputStream,
                currentUser
        );
    }

    // Ticket'ın doğrudan ve yorumlara bağlı tüm eklerini getir
    public List<Attachment> getAttachmentsByTicket(
            Long ticketId,
            User currentUser) {

        return attachmentService.getAttachmentsByTicket(
                ticketId,
                currentUser
        );
    }

    // İndirme öncesi erişim kontrolü
    public Attachment getAttachmentForDownload(
            Long attachmentId,
            User currentUser) {

        return attachmentService.getAttachmentForDownload(
                attachmentId,
                currentUser
        );
    }

    // Dosya sil
    public void deleteAttachment(
            Long attachmentId,
            User currentUser) {

        attachmentService.deleteAttachment(
                attachmentId,
                currentUser
        );
    }
}
