package com.erdal.helpdeskpro.dtos;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.enums.CommentType;

public class CommentDTO {

	private Long id;
	private String content;
	private Long ticketId;
	private Long authorId;
	private boolean deleted;
	private CommentType type;
	private String authorUsername;
	private String authorRole;
	private String authorPhotoUrl;
	private LocalDateTime createdAt=LocalDateTime.now();

	public CommentDTO() {
		super();
	}

	
	public CommentDTO(Long id, String content, Long ticketId, Long authorId, boolean deleted, CommentType type,
			String authorUsername, String authorRole, String authorPhotoUrl, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.content = content;
		this.ticketId = ticketId;
		this.authorId = authorId;
		this.deleted = deleted;
		this.type = type;
		this.authorUsername = authorUsername;
		this.authorRole = authorRole;
		this.authorPhotoUrl = authorPhotoUrl;
		this.createdAt = createdAt;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
		this.type = type;
	}


	public String getAuthorUsername() {
		return authorUsername;
	}


	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}


	public String getAuthorRole() {
		return authorRole;
	}


	public void setAuthorRole(String authorRole) {
		this.authorRole = authorRole;
	}


	public String getAuthorPhotoUrl() {
		return authorPhotoUrl;
	}


	public void setAuthorPhotoUrl(String authorPhotoUrl) {
		this.authorPhotoUrl = authorPhotoUrl;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
