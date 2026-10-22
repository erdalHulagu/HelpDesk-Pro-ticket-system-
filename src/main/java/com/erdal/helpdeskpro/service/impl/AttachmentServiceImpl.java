
package com.erdal.helpdeskpro.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.AttachmentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.service.AttachmentService;

public class AttachmentServiceImpl implements AttachmentService {

	private static final Path UPLOAD_DIR = Path.of("uploads").toAbsolutePath().normalize();

	private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

	private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "image/png", "image/jpeg", "image/jpg",
			"text/plain");

	private final AttachmentRepository attachmentRepository;
	private final TicketRepository ticketRepository;
	private final Authorization authorization;
	private final CommentRepository commentRepository;

	public AttachmentServiceImpl(AttachmentRepository attachmentRepository, TicketRepository ticketRepository,
			Authorization authorization,CommentRepository commentRepository) {

		this.attachmentRepository = attachmentRepository;
		this.ticketRepository = ticketRepository;
		this.authorization = authorization;
		this.commentRepository = commentRepository;
	}
	
	private Path storeUploadedFile(String fileName, InputStream inputStream) {
	    if (fileName == null || fileName.isBlank() || inputStream == null) {
	        throw new IllegalArgumentException("Invalid upload parameters");
	    }

	    String safeName = Path.of(fileName.replace('\\', '/'))
	            .getFileName()
	            .toString();

	    if (safeName.isBlank()) {
	        throw new IllegalArgumentException("Invalid file name");
	    }

	    Path savedPath = null;

	    try {
	        Files.createDirectories(UPLOAD_DIR);

	        savedPath = UPLOAD_DIR.resolve(UUID.randomUUID().toString())
	                .normalize();

	        if (!savedPath.startsWith(UPLOAD_DIR)) {
	            throw new SecurityException("Invalid storage path");
	        }

	        try (InputStream source = inputStream) {
	            Files.copy(new LimitedInputStream(source, MAX_FILE_SIZE), savedPath);
	        }

	        String detectedType = detectFileType(savedPath);

	        if (detectedType == null || !ALLOWED_TYPES.contains(detectedType)) {
	            throw new IllegalArgumentException("Unsupported or invalid file format");
	        }

	        validateFileSignature(savedPath, detectedType);

	        return savedPath;

	    } catch (IOException | RuntimeException e) {
	        if (savedPath != null) {
	            try {
	                Files.deleteIfExists(savedPath);
	            } catch (IOException cleanupError) {
	                e.addSuppressed(cleanupError);
	            }
	        }

	        throw new RuntimeException("Attachment upload failed", e);
	    }
	}


@Override
public Attachment uploadForTicket(
        Long ticketId,
        String fileName,
        String contentType,
        InputStream inputStream,
        User currentUser) {

    if (ticketId == null || ticketId <= 0
            || fileName == null || fileName.isBlank()
            || inputStream == null || currentUser == null) {
        throw new IllegalArgumentException("Invalid upload parameters");
    }

    Ticket ticket = ticketRepository.findById(ticketId);

    if (ticket == null) {
        throw new IllegalArgumentException("Ticket not found");
    }

    authorization.canViewTicket(ticket, currentUser);

    Path savedPath = storeUploadedFile(fileName, inputStream);

    try {
        Attachment attachment = new Attachment();

        attachment.setTicket(ticket);
        attachment.setUser(null);
        attachment.setFileName(
                Path.of(fileName.replace('\\', '/'))
                        .getFileName().toString()
        );

        // Dosya türü storeUploadedFile içinde doğrulandı.
        // Burada yeniden okumaya gerek yok.
        String detectedType = detectFileType(savedPath);
        attachment.setFileType(detectedType);

        attachment.setFilePath(savedPath.toString());
        attachment.setUploadedAt(LocalDateTime.now());

        return attachmentRepository.saveTicketAttachment(attachment);

    } catch (IOException | RuntimeException e) {
        try {
            Files.deleteIfExists(savedPath);
        } catch (IOException cleanupError) {
            e.addSuppressed(cleanupError);
        }

        throw new RuntimeException("Failed to save ticket attachment", e);
    }
}

	@Override
	public Attachment uploadForUser(

			String fileName, String contentType, InputStream inputStream, User currentUser) {

		if (fileName == null || fileName.isBlank() || inputStream == null || currentUser == null) {
			throw new IllegalArgumentException("Invalid upload parameters");
		}
		String safeName = Path.of(fileName.replace('\\', '/')).getFileName().toString();

		if (safeName.isBlank()) {
			throw new IllegalArgumentException("Invalid file name");
		}

		Path savedPath = null;

		try {
			Files.createDirectories(UPLOAD_DIR);

			savedPath = UPLOAD_DIR.resolve(UUID.randomUUID().toString()).normalize();

			if (!savedPath.startsWith(UPLOAD_DIR)) {
				throw new SecurityException("Invalid storage path");
			}

			try (InputStream source = inputStream) {
				Files.copy(new LimitedInputStream(source, MAX_FILE_SIZE), savedPath);
			}

			// Dosya türünü HTTP Content-Type başlığına bağlı kalmadan tespit et.
			String detectedType = detectFileType(savedPath);

			if (detectedType == null || !ALLOWED_TYPES.contains(detectedType)) {
				throw new IllegalArgumentException("Unsupported or invalid file format");
			}

			// Yalnızca bir Attachment oluştur ve kaydet.
			Attachment attachment = new Attachment();

			attachment.setTicket(null);
			attachment.setUser(currentUser);

			attachment.setFileName(safeName);
			attachment.setFileType(detectedType);
			attachment.setFilePath(savedPath.toString());
			attachment.setUploadedAt(LocalDateTime.now());

			return attachmentRepository.saveUserAttachment(attachment);

		} catch (IOException | RuntimeException e) {
			if (savedPath != null) {
				try {
					Files.deleteIfExists(savedPath);
				} catch (IOException cleanupError) {
					e.addSuppressed(cleanupError);
				}
			}

			throw new RuntimeException("Attachment upload failed", e);
		}
	};

	private void validateFileSignature(Path path, String type) throws IOException {

		byte[] header = new byte[8];

		int count;

		try (InputStream input = Files.newInputStream(path)) {
			count = input.read(header);
		}

		boolean valid = switch (type) {
		case "application/pdf" -> count >= 5 && header[0] == '%' && header[1] == 'P' && header[2] == 'D'
				&& header[3] == 'F' && header[4] == '-';

		case "image/png" -> count >= 8 && (header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N'
				&& header[3] == 'G' && (header[4] & 0xFF) == 0x0D && (header[5] & 0xFF) == 0x0A
				&& (header[6] & 0xFF) == 0x1A && (header[7] & 0xFF) == 0x0A;

		case "image/jpeg" ->
			count >= 3 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;

		case "text/plain" -> count >= 0;

		default -> false;
		};

		if (!valid) {
			throw new IllegalArgumentException("File content does not match its declared type");
		}
	}

	private String detectFileType(Path path) throws IOException {

		byte[] header = new byte[8];

		try (InputStream input = Files.newInputStream(path)) {

			int count = input.read(header);

			// PDF
			if (count >= 5 && header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F'
					&& header[4] == '-') {
				return "application/pdf";
			}

			// PNG
			if (count >= 8 && (header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
					&& (header[4] & 0xFF) == 0x0D && (header[5] & 0xFF) == 0x0A && (header[6] & 0xFF) == 0x1A
					&& (header[7] & 0xFF) == 0x0A) {
				return "image/png";
			}

			// JPG / JPEG
			if (count >= 3 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
				return "image/jpeg";
			}
		}

		return null;
	}

	@Override
	public List<Attachment> getAttachmentsByTicket(Long ticketId, User currentUser) {

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

	@Override
	public Attachment getAttachmentForDownload(Long attachmentId, User currentUser) {

		if (attachmentId == null || currentUser == null) {
			throw new IllegalArgumentException("Invalid parameters");
		}

		Attachment attachment = attachmentRepository.findById(attachmentId);

		if (attachment == null) {
			throw new IllegalArgumentException("Attachment not found");
		}

		// Ek bir bilete mi, kullanıcıya mı ait kontrol et.
		if (attachment.getTicket() != null) {

			Ticket ticket = ticketRepository.findById(attachment.getTicket().getId());

			if (ticket == null) {
				throw new IllegalArgumentException("Ticket not found");
			}

			authorization.canViewTicket(ticket, currentUser);

		} else if (attachment.getUser() != null) {

			if (attachment.getUser().getId() == null || !attachment.getUser().getId().equals(currentUser.getId())) {

				throw new SecurityException("You cannot access this user attachment");
			}

		} else {
			throw new IllegalArgumentException("Attachment has no owner");
		}

		// Dosyanın yolunu doğrula.
		Path path = Path.of(attachment.getFilePath()).toAbsolutePath().normalize();

		if (!path.startsWith(UPLOAD_DIR)) {
			throw new SecurityException("Invalid attachment path");
		}

		if (!Files.isRegularFile(path)) {
			throw new IllegalArgumentException("File not found on disk");
		}

		return attachment;
	}

	@Override
	public void deleteAttachment(Long attachmentId, User currentUser) {

		Attachment attachment = getAttachmentForDownload(attachmentId, currentUser);

		Path path = Path.of(attachment.getFilePath()).toAbsolutePath().normalize();

		try {
			Files.deleteIfExists(path);
			attachmentRepository.deleteById(attachmentId);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete attachment", e);
		}
	}

}
