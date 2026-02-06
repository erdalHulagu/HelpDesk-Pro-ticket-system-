package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Table(name = "comments")
public class  Comment{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne()
	@JoinColumn(name = "ticket_id")
	private Ticket ticketId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User author;
	
	private String message;
	
	private LocalDateTime createdAt;
	
	public  Comment() {
	
		
	}

	

	public Comment(Long id, Ticket ticketId, User author, String message, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.ticketId = ticketId;
		this.author = author;
		this.message = message;
		this.createdAt = createdAt;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Ticket getTicketId() {
		return ticketId;
	}



	public void setTicketId(Ticket ticketId) {
		this.ticketId = ticketId;
	}



	public User getAuthor() {
		return author;
	}



	public void setAuthor(User author) {
		this.author = author;
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