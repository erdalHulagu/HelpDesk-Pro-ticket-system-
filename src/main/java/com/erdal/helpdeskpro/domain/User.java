package com.erdal.helpdeskpro.domain;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.erdal.helpdeskpro.enums.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

public class User {
	
	private Long id;
	private String username;
	private String email;
	private String password;
	
	@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
	private List<Ticket> createdTickets= new ArrayList<>();
	
	@OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
	private List<Ticket> assignedTickets = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	private Role role;
	private boolean isActive;
	private LocalDateTime createdAt = LocalDateTime.now();
	
	public User() {
	}
	public User(Long id, String username, String email, String password) {
		this.id=id;
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	
	public User(Long id, String username, String email, String password, List<Ticket> createdTickets,
			List<Ticket> assignedTickets, Role role, boolean isActive, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.createdTickets = createdTickets;
		this.assignedTickets = assignedTickets;
		this.role = role;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public List<Ticket> getCreatedTickets() {
		return createdTickets;
	}
	public void setCreatedTickets(List<Ticket> createdTickets) {
		this.createdTickets = createdTickets;
	}
	public List<Ticket> getAssignedTickets() {
		return assignedTickets;
	}
	public void setAssignedTickets(List<Ticket> assignedTickets) {
		this.assignedTickets = assignedTickets;
	}
	public String getRole() {
		
		//for SQL database connection we need string  thats why we send name() method which returns string
		return role != null ? role.name() : null;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
	
}
