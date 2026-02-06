package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name="attachments")
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticked_id")
	private Ticket ticket;
	
	private String fileName;
	
	private String fileType;
	
	private String filePath;
	
	private LocalDateTime uploadedAt;

	public Attachment() {
	}

	

	public Attachment(Long id, Ticket ticket, String fileName, String fileType, String filePath,
			LocalDateTime uploadedAt) {
		super();
		this.id = id;
		this.ticket = ticket;
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



	public Ticket getTicket() {
		return ticket;
	}



	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
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
