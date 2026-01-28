package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

public class  Common{
	
	private Long id;
	private Long ticketId;
	private Long userId;
	private String message;
	private LocalDateTime createdAt;
	
	public  Common() {
	
		
	}

	public  Common(Long id, Long ticketId, Long userId, String message, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.ticketId = ticketId;
		this.userId = userId;
		this.message = message;
		this.createdAt = createdAt;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
	
}
