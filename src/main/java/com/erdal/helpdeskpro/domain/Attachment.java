package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="attachments")
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "ticket_id", nullable = true)
	private Ticket ticket;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "user_id", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "comment_id", nullable = true)
	private Comment comment;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "file_type", nullable = false)
	private String fileType;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	@Column(name = "uploaded_at", nullable = false)
	private LocalDateTime uploadedAt = LocalDateTime.now();

	public Attachment() {
	}

	

	public Attachment(Long id, Ticket ticket, Comment comment, String fileName, String fileType, String filePath,User user,
			LocalDateTime uploadedAt) {
		super();
		this.id = id;
		this.ticket = ticket;
		this.comment = comment;
		this.fileName = fileName;
		this.fileType = fileType;
		this.filePath = filePath;
		this.user=user;
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

	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}

	public String getFileName() {
		return fileName;
	}

	public Comment getComment() {
	    return comment;
	}

	public void setComment(Comment comment) {
	    this.comment = comment;
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
