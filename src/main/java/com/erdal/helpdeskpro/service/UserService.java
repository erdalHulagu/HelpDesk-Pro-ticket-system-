package com.erdal.helpdeskpro.service;

import java.util.List;
import java.util.Optional;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.exception.UserExceptionMessage;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.request.UserRequest;

public interface UserService {
	
	void createUser(String username, String email, String password, Role role);
	
	User getUser(Long id);
	
	List<User> getAllUsers();
	

}
