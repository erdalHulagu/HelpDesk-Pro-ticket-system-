package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

public class Attachment {

	private Long id;
	private Long ticketId;
	private String fileName;
	private String fileType;
	private String filePath;
	private LocalDateTime uploadedAt;

	public Attachment() {
	}

	public Attachment(Long id, Long ticketId, String fileName, String fileType, String filePath,
			LocalDateTime uploadedAt) {
		super();
		this.id = id;
		this.ticketId = ticketId;
		this.fileName = fileName;
		this.fileType = fileType;
		this.filePath = filePath;
		this.uploadedAt = uploadedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

}
