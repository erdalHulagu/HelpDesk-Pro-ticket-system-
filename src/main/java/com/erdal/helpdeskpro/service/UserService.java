package com.erdal.helpdeskpro.service;

import java.util.List;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;

public interface UserService {
	
	void createUser(String username, String email, String password, Role role);
	
	User getUser(Long id);
	
	List<User> getAllUsers();
	
	void deactivateUser(Long id);
	
	User validateLogin(String username, String password);

}
