package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.User;

public class UserRepository {
	
	public void save(User user) {
		
		
	}
	
	
	public User findById(Long id) {
		
		return null;
		
	};
	
	public User findByEmail(String email) {
		return null;
	};
	public boolean existsByEmail(String email) {
		return true;
	};
	
	public List<User> findAll() {
		return null;
	}

}
