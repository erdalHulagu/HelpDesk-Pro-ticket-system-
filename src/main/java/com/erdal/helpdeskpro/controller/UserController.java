package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.mapper.UserMapper;
import com.erdal.helpdeskpro.service.UserService;

public class UserController {
	 
	private final UserService userService;
	
	
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	public void register(UserDTO userDTO) {
	 	
	 	userService.createUser(UserMapper.userDTOtoUser(userDTO));
	}
	
	public UserDTO findUserById(Long id) {
		
		User user=userService.getUser(id);
		
		return  UserMapper.userToUserDTO(user);
		
	}
	public List<UserDTO> findAllUsers() {
		
		 List<User> users=userService.getAllUsers();
		
		return  UserMapper.userListToUserDTOList(users);
		
	}
	public UserDTO updateUser(User currentUser) {
		
		
		User usr=userService.updateUser(currentUser);
		
		return UserMapper.userToUserDTO(usr);
		
		
		
	}
	public UserDTO login(String username, String password) {
		
		User user=userService.login(username,password);
		
		return UserMapper.userToUserDTO(user);
		
		
		
	}
	
	public void deActiveUser(Long id) {
		
		userService.deactivateUser(id);
		
		
		
	}
	

}
