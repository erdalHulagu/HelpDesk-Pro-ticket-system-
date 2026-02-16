package com.erdal.helpdeskpro.service;

import java.util.List;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.enums.Role;

public interface UserService {
	
	void createUser(User user);
	
	User getUser(Long id);
	
	List<User> getAllUsers();
	
	void deactivateUser(Long id);
	
	User login(String username, String password);

	User updateUser(User user);

}
