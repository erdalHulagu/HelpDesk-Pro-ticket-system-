package com.erdal.helpdeskpro;

import java.time.LocalDateTime;

public class UserDTO {
	private Long id;
	private String userName;
	private String email;
	private String password;
	private String role;
	private boolean isActive;
	private LocalDateTime createdAt;

	public UserDTO() {
	}

	public UserDTO(Long id, String username, String email, String password, String role, boolean isActive,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.userName = username;
		this.email = email;
		this.password = password;
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
		return userName;
	}

	public void setUsername(String username) {
		this.userName = username;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
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
