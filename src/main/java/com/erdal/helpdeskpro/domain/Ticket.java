package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.enums.TicketCategory;
import com.erdal.helpdeskpro.enums.TicketStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Ticket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private TicketCategory category;
	
	@Enumerated(EnumType.STRING)
	private TicketStatus status;
	
	 @ManyToOne
	 @JoinColumn(name = "created_by")
	private User createdBy;
	
	 @ManyToOne
	 @JoinColumn(name = "assigned_to")
	private User assignedTo;
	 
	private LocalDateTime createdAt;
	private boolean isDeleted;
	
	public Ticket() {
		super();
	}

	public Ticket(Long id, String title, String description, TicketCategory category, TicketStatus status,
			User createdBy, User assignedTo, LocalDateTime createdAt, boolean isDeleted) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.status = status;
		this.createdBy = createdBy;
		this.assignedTo = assignedTo;
		this.createdAt = createdAt;
		this.isDeleted = isDeleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TicketCategory getCategory() {
		return category;
	}

	public void setCategory(TicketCategory category) {
		this.category = category;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	

}
