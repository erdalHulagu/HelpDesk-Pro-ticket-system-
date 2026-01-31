package com.erdal.helpdeskpro.service;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
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

}
