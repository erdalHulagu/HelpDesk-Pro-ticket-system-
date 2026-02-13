package com.erdal.helpdeskpro.dtos;

import com.erdal.helpdeskpro.enums.TicketStatus;

public class TicketDTO {
	private Long id;
	private String title;
	private String description;
	private TicketStatus status;
	private Long createdById;
	private Long assignedToId;
	private boolean deleted;

	public TicketDTO() {
		super();
	}

	public TicketDTO(Long id, String title, String description, TicketStatus status, Long createdById,
			Long assignedToId, boolean deleted) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
		this.createdById = createdById;
		this.assignedToId = assignedToId;
		this.deleted = deleted;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
