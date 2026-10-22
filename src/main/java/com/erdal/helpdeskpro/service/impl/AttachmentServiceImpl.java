
package com.erdal.helpdeskpro.service.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AttachmentType;
import com.erdal.helpdeskpro.repository.AttachmentRepository;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.service.AttachmentService;

public class AttachmentServiceImpl implements AttachmentService {

    private static final Path UPLOAD_DIR =
            Path.of("uploads").toAbsolutePath().normalize();

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg"
    );

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final Authorization authorization;

    public AttachmentServiceImpl(
            AttachmentRepository attachmentRepository,
            TicketRepository ticketRepository,
            Authorization authorization,
            CommentRepository commentRepository) {

        this.attachmentRepository = attachmentRepository;
        this.ticketRepository = ticketRepository;
        this.authorization = authorization;
        this.commentRepository = commentRepository;
    }

    // Dosya yolu ve tespit edilen tür birlikte tutulur.
    private record StoredFile(Path path, String contentType) {
    }

    // --------------------------------------------------
    // Common file storage and validation
    // --------------------------------------------------

    private StoredFile storeUploadedFile(
            String fileName,
            InputStream inputStream) {

        if (fileName == null || fileName.isBlank() || inputStream == null) {
            throw new IllegalArgumentException("Invalid upload parameters");
        }

        safeFileName(fileName);

        Path savedPath = null;

        try {
            Files.createDirectories(UPLOAD_DIR);

            savedPath = UPLOAD_DIR
                    .resolve(UUID.randomUUID().toString())
                    .normalize();

            if (!savedPath.startsWith(UPLOAD_DIR)) {
                throw new SecurityException("Invalid storage path");
            }

            try (InputStream source = inputStream) {
                Files.copy(
                        new LimitedInputStream(source, MAX_FILE_SIZE),
                        savedPath
                );
            }

            String detectedType = detectFileType(savedPath);

            if (detectedType == null
                    || !ALLOWED_TYPES.contains(detectedType)) {
                throw new IllegalArgumentException(
                        "Unsupported or invalid file format"
                );
            }

            validateFileSignature(savedPath, detectedType);

            return new StoredFile(savedPath, detectedType);

        } catch (IOException | RuntimeException e) {
            if (savedPath != null) {
                deleteStoredFileAfterFailure(savedPath, e);
            }

            throw new RuntimeException("Attachment upload failed", e);
        }
    }

    private String safeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String safeName = Path.of(fileName.replace('\\', '/'))
                .getFileName()
                .toString();

        if (safeName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        return safeName;
    }

    // Bu metot sınıf seviyesinde; LimitedInputStream içinde değildir.
    private void deleteStoredFileAfterFailure(
            Path savedPath,
            Throwable error) {

        try {
            Files.deleteIfExists(savedPath);
        } catch (IOException cleanupError) {
            error.addSuppressed(cleanupError);
        }
    }

    // --------------------------------------------------
    // Ticket attachments
    // --------------------------------------------------

    @Override
    public Attachment uploadForTicket(
            Long ticketId,
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        if (ticketId == null || ticketId <= 0 || currentUser == null) {
            throw new IllegalArgumentException("Invalid upload parameters");
        }

        String safeName = safeFileName(fileName);

        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        authorization.canViewTicket(ticket, currentUser);

        StoredFile storedFile = storeUploadedFile(safeName, inputStream);

        try {
            Attachment attachment = new Attachment();

            attachment.setTicket(ticket);
            attachment.setAttachmentType(AttachmentType.TICKET_FILE);
            attachment.setUser(null);
            attachment.setComment(null);
            attachment.setFileName(safeName);
            attachment.setFileType(storedFile.contentType());
            attachment.setFilePath(storedFile.path().toString());
            attachment.setUploadedAt(LocalDateTime.now());

            return attachmentRepository.saveTicketAttachment(attachment);

        } catch (RuntimeException e) {
            deleteStoredFileAfterFailure(storedFile.path(), e);
            throw new RuntimeException(
                    "Failed to save ticket attachment", e);
        }
    }

    // --------------------------------------------------
    // Comment attachments
    // --------------------------------------------------

    @Override
    public Attachment uploadForComment(
            Long commentId,
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        if (commentId == null || commentId <= 0 || currentUser == null) {
            throw new IllegalArgumentException("Invalid upload parameters");
        }

        String safeName = safeFileName(fileName);

        Comment comment = commentRepository.findById(commentId);

        if (comment == null || comment.isDeleted()) {
            throw new IllegalArgumentException("Comment not found");
        }

        Ticket ticket = comment.getTicket();

        if (ticket == null) {
            throw new IllegalArgumentException("Comment has no ticket");
        }

        authorization.canViewTicket(ticket, currentUser);

        StoredFile storedFile = storeUploadedFile(safeName, inputStream);

        try {
            Attachment attachment = new Attachment();

            attachment.setTicket(null);
            attachment.setUser(null);
            attachment.setComment(comment);
            attachment.setAttachmentType(AttachmentType.COMMENT_FILE);
            attachment.setFileName(safeName);
            attachment.setFileType(storedFile.contentType());
            attachment.setFilePath(storedFile.path().toString());
            attachment.setUploadedAt(LocalDateTime.now());

            return attachmentRepository.saveCommentAttachment(attachment);

        } catch (RuntimeException e) {
            deleteStoredFileAfterFailure(storedFile.path(), e);
            throw new RuntimeException(
                    "Comment attachment upload failed", e);
        }
    }

    // --------------------------------------------------
    // User attachments
    // --------------------------------------------------

    @Override
    public Attachment uploadForUser(
            String fileName,
            String contentType,
            InputStream inputStream,
            User currentUser) {

        if (currentUser == null) {
            throw new IllegalArgumentException("Invalid upload parameters");
        }

        String safeName = safeFileName(fileName);

        StoredFile storedFile = storeUploadedFile(safeName, inputStream);

        try {
            Attachment attachment = new Attachment();

            attachment.setTicket(null);
            attachment.setUser(currentUser);
            attachment.setAttachmentType(AttachmentType.USER_FILE);
            attachment.setComment(null);
            attachment.setFileName(safeName);
            attachment.setFileType(storedFile.contentType());
            attachment.setFilePath(storedFile.path().toString());
            attachment.setUploadedAt(LocalDateTime.now());

            return attachmentRepository.saveUserAttachment(attachment);

        } catch (RuntimeException e) {
            deleteStoredFileAfterFailure(storedFile.path(), e);
            throw new RuntimeException(
                    "User attachment upload failed", e);
        }
    }

    // --------------------------------------------------
    // List ticket attachments
    // --------------------------------------------------

    @Override
    public List<Attachment> getAttachmentsByTicket(
            Long ticketId,
            User currentUser) {

        if (ticketId == null || currentUser == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        authorization.canViewTicket(ticket, currentUser);

        return attachmentRepository.findByTicketId(ticketId);
    }

    // --------------------------------------------------
    // Download attachment
    // --------------------------------------------------

    @Override
    public Attachment getAttachmentForDownload(
            Long attachmentId,
            User currentUser) {

        if (attachmentId == null || currentUser == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        Attachment attachment = attachmentRepository.findById(attachmentId);

        if (attachment == null) {
            throw new IllegalArgumentException("Attachment not found");
        }

        if (attachment.getTicket() != null) {

            Ticket ticket = ticketRepository.findById(
                    attachment.getTicket().getId());

            if (ticket == null) {
                throw new IllegalArgumentException("Ticket not found");
            }

            authorization.canViewTicket(ticket, currentUser);

        } else if (attachment.getComment() != null) {

            Comment comment = commentRepository.findById(
                    attachment.getComment().getId());

            if (comment == null || comment.isDeleted()) {
                throw new IllegalArgumentException("Comment not found");
            }

            Ticket ticket = comment.getTicket();

            if (ticket == null) {
                throw new IllegalArgumentException("Comment has no ticket");
            }

            authorization.canViewTicket(ticket, currentUser);

        } else if (attachment.getUser() != null) {

            if (attachment.getUser().getId() == null
                    || !attachment.getUser().getId()
                            .equals(currentUser.getId())) {
                throw new SecurityException(
                        "You cannot access this user attachment");
            }

        } else {
            throw new IllegalArgumentException("Attachment has no owner");
        }

        Path path = Path.of(attachment.getFilePath())
                .toAbsolutePath()
                .normalize();

        if (!path.startsWith(UPLOAD_DIR)) {
            throw new SecurityException("Invalid attachment path");
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("File not found on disk");
        }

        return attachment;
    }
    
    
    // --------------------------------------------------
    // Get Profile attachment
    // --------------------------------------------------
    @Override
    public Attachment findProfilePhoto(Long userId) {
        return attachmentRepository.findProfilePhoto(userId);
    }

    // --------------------------------------------------
    // Delete attachment
    // --------------------------------------------------

    @Override
    public void deleteAttachment(Long attachmentId, User currentUser) {

        Attachment attachment =
                getAttachmentForDownload(attachmentId, currentUser);

        Path path = Path.of(attachment.getFilePath())
                .toAbsolutePath()
                .normalize();

        try {
            Files.deleteIfExists(path);
            attachmentRepository.deleteById(attachmentId);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete attachment", e);
        }
    }

    // --------------------------------------------------
    // File signature validation
    // --------------------------------------------------

    private void validateFileSignature(Path path, String type)
            throws IOException {

        byte[] header = new byte[8];
        int count;

        try (InputStream input = Files.newInputStream(path)) {
            count = input.read(header);
        }

        boolean valid = switch (type) {
            case "application/pdf" ->
                    count >= 5
                    && header[0] == '%'
                    && header[1] == 'P'
                    && header[2] == 'D'
                    && header[3] == 'F'
                    && header[4] == '-';

            case "image/png" ->
                    count >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 'P'
                    && header[2] == 'N'
                    && header[3] == 'G'
                    && (header[4] & 0xFF) == 0x0D
                    && (header[5] & 0xFF) == 0x0A
                    && (header[6] & 0xFF) == 0x1A
                    && (header[7] & 0xFF) == 0x0A;

            case "image/jpeg" ->
                    count >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;

            default -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "File content does not match its detected type");
        }
    }

    // --------------------------------------------------
    // Detect file type
    // --------------------------------------------------

    private String detectFileType(Path path) throws IOException {

        byte[] header = new byte[8];

        try (InputStream input = Files.newInputStream(path)) {
            int count = input.read(header);

            if (count >= 5
                    && header[0] == '%'
                    && header[1] == 'P'
                    && header[2] == 'D'
                    && header[3] == 'F'
                    && header[4] == '-') {
                return "application/pdf";
            }

            if (count >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 'P'
                    && header[2] == 'N'
                    && header[3] == 'G'
                    && (header[4] & 0xFF) == 0x0D
                    && (header[5] & 0xFF) == 0x0A
                    && (header[6] & 0xFF) == 0x1A
                    && (header[7] & 0xFF) == 0x0A) {
                return "image/png";
            }

            if (count >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF) {
                return "image/jpeg";
            }
        }

        return null;
    }

    // --------------------------------------------------
    // Maximum upload size limiter
    // --------------------------------------------------

    private static class LimitedInputStream extends FilterInputStream {

        private final long maxBytes;
        private long bytesRead;

        protected LimitedInputStream(InputStream input, long maxBytes) {
            super(input);
            this.maxBytes = maxBytes;
        }

        @Override
        public int read() throws IOException {
            int value = super.read();

            if (value != -1) {
                bytesRead++;

                if (bytesRead > maxBytes) {
                    throw new IOException("Maximum upload size exceeded");
                }
            }

            return value;
        }

        @Override
        public int read(byte[] buffer, int offset, int length)
                throws IOException {

            if (length == 0) {
                return 0;
            }

            long remaining = maxBytes - bytesRead;

            if (remaining < 0) {
                throw new IOException("Maximum upload size exceeded");
            }

            int allowedLength = (int) Math.min(length, remaining + 1);
            int count = super.read(buffer, offset, allowedLength);

            if (count > 0) {
                bytesRead += count;

                if (bytesRead > maxBytes) {
                    throw new IOException("Maximum upload size exceeded");
                }
            }

            return count;
        }
    }
}
