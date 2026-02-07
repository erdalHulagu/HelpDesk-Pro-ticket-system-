package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.repository.UserRepository;

public class UserServiceImpl {
	
	private final UserRepository userRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository=userRepository;
		
	}

}
