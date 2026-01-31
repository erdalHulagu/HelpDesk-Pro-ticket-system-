package com.erdal.helpdeskpro.controller;

import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.mapper.UserMapper;
import com.erdal.helpdeskpro.service.UserService;

public class UserController {
	 
	private final UserService userService;

	
	public UserController() {
		this.userService = new UserService();
	}
	
	public void register(UserDTO userDTO) {
	 	
	 	userService.registerUser(UserMapper.userDTOtoUser(userDTO));
	}

}
