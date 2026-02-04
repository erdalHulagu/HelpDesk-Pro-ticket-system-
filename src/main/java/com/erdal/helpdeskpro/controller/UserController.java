package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.mapper.UserMapper;
import com.erdal.helpdeskpro.request.UserRequest;
import com.erdal.helpdeskpro.service.UserService;

public class UserController {
	 
	private final UserService userService;
	
	
	
	public UserController() {
		this.userService = new UserService();
	}
	
	public void register(UserDTO userDTO) {
	 	
	 	userService.registerUser(UserMapper.userDTOtoUser(userDTO));
	}
	
	public UserDTO findUserById(Long id) {
		
		User user=userService.findById(id);
		
		return  UserMapper.userToUserDTO(user);
		
	}
	public List<UserDTO> findAllUsers() {
		
		 List<User> users=userService.findAll();
		
		return  UserMapper.userListToUserDTOList(users);
		
	}
	public UserDTO updateUser(UserRequest userRequest) {
		
		User user=userService.updateUser(userRequest);
		
		return UserMapper.userToUserDTO(user);
		
		
		
	}
	public UserDTO findUserByEmail(String email) {
		
		User user=userService.findUserByEmail(email);
		
		return UserMapper.userToUserDTO(user);
		
		
		
	}
	

}
