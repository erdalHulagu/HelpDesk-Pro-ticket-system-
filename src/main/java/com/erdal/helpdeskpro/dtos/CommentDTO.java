package com.erdal.helpdeskpro.dtos;

public class CommentDTO {

	private Long id;
	private String content;
	private Long ticketId;
	private Long authorId;
	private boolean deleted;

	public CommentDTO() {
		super();
	}

	public CommentDTO(Long id, String content, Long ticketId, Long authorId, boolean deleted) {
		super();
		this.id = id;
		this.content = content;
		this.ticketId = ticketId;
		this.authorId = authorId;
		this.deleted = deleted;
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

}
