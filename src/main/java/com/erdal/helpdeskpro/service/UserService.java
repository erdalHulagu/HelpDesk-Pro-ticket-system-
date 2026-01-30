package com.erdal.helpdeskpro.service;

import com.erdal.helpdeskpro.repository.UserRepository;

public class UserService {
	
   	UserRepository userRepository=new UserRepository();
	
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
		
	}
	
	public void registerUser() {
		
		
		
	}

}
