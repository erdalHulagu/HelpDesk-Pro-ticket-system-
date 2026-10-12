package com.erdal.helpdeskpro.service;

import java.util.List;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.enums.Role;

public interface UserService {
	
	User getUser(Long id);
	
	void deactivateUser(Long id);
	
	User login(String email, String password);

	User updateUser(User user);

	List<User> getAllUsers(User currentUser);

	List<User> findAllUsers();

	void registerUser(User user);

}
