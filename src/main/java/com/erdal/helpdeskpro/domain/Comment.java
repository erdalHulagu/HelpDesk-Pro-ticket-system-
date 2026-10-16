package com.erdal.helpdeskpro.domain;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.enums.CommentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "ticket_id")
	private Ticket ticket;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User author;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CommentType type = CommentType.PUBLIC;

	

	public Comment() {

	}

	public Comment(Long id, Ticket ticket, User author, String content, LocalDateTime createdAt, boolean isDeleted,
			CommentType type) {
		super();
		this.id = id;
		this.ticket = ticket;
		this.author = author;
		this.content = content;
		this.createdAt = createdAt;
		this.isDeleted = isDeleted;
		this.type = type;
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

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
	
	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
		this.type = type;
	}

}