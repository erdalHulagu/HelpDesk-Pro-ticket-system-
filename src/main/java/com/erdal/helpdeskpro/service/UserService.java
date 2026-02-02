package com.erdal.helpdeskpro.service;

import java.util.List;
import java.util.Optional;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.exception.UserExceptionMessage;
import com.erdal.helpdeskpro.repository.UserRepository;

public class UserService {
	
   	private final UserRepository userRepository;
	
	public UserService() {
		this.userRepository=new UserRepository();
		
	}
	
	public void registerUser(User user) {
		
		if (user==null) {
			throw new BadRequestExeption(UserExceptionMessage.USER_IS_NULL);
		}
		user.setRole(Role.EMPLOYEE);
		user.setActive(true);
		userRepository.save(user);
		
	}
	
	public  User findById(Long id) {
			                                               
		return userRepository.findById(id).orElseThrow(()-> new  ResourceNotFoundExeption(UserExceptionMessage.USER_NOT_FOUND));
	}

	public List<User> findAll() {
		
		List<User> users=userRepository.findAll();
		
		if (users.isEmpty()) {
			
			throw new ResourceNotFoundExeption( UserExceptionMessage.NO_USERS);
			
		}
		return users;
	}

}
