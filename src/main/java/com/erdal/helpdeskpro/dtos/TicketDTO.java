package com.erdal.helpdeskpro.dtos;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.enums.TicketCategory;
import com.erdal.helpdeskpro.enums.TicketPriority;
import com.erdal.helpdeskpro.enums.TicketStatus;

public class TicketDTO {
	private Long id;
	private String title;
	private String description;
	private TicketCategory category;
	private TicketStatus status;
	private TicketPriority priority;
	private Long createdById;
	private Long assignedToId;
	private String createdByUsername;
	private String createdByRole;
	private LocalDateTime slaDueAt;
	private LocalDateTime resolvedAt;
	private boolean isDeleted;

	public TicketDTO() {
		super();
	}


	public TicketDTO(Long id, String title, String description, TicketCategory category, TicketStatus status,
			TicketPriority priority, Long createdById, Long assignedToId, String createdByUsername,
			String createdByRole, LocalDateTime slaDueAt, LocalDateTime resolvedAt, boolean isDeleted) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.status = status;
		this.priority = priority;
		this.createdById = createdById;
		this.assignedToId = assignedToId;
		this.createdByUsername = createdByUsername;
		this.createdByRole = createdByRole;
		this.slaDueAt = slaDueAt;
		this.resolvedAt = resolvedAt;
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

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}
	public TicketCategory getCategory() {
		return category;
	}
	
	public void setCategory(TicketCategory category) {
		this.category = category;
	}
	public TicketPriority getPriority() {
		return priority;
	}
	
	public void setPriority(TicketPriority priority) {
		this.priority = priority;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Long getAssignedToId() {
		return assignedToId;
	}

	public void setAssignedToId(Long assignedToId) {
		this.assignedToId = assignedToId;
	}

	public String getCreatedByUsername() {
		return createdByUsername;
	}

	public void setCreatedByUsername(String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	public String getCreatedByRole() {
		return createdByRole;
	}

	public void setCreatedByRole(String createdByRole) {
		this.createdByRole = createdByRole;
	}
	
	

	public LocalDateTime getSlaDueAt() {
		return slaDueAt;
	}


	public void setSlaDueAt(LocalDateTime slaDueAt) {
		this.slaDueAt = slaDueAt;
	}


	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}


	public void setResolvedAt(LocalDateTime resolvedAt) {
		this.resolvedAt = resolvedAt;
	}


	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		this.isDeleted = deleted;
	}

}
