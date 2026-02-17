package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.service.UserService;

public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository=userRepository;
		
	}

	@Override
	public void createUser(User user) {
		
		User craeteduser=new User();
		craeteduser.setUsername(user.getUsername());
		craeteduser.setEmail(user.getEmail());
		craeteduser.setPassword(user.getPassword());
		craeteduser.setRole(user.getRole());
		craeteduser.setActive(true);
		
		userRepository.save(craeteduser);
		
	}

	@Override
	public User getUser(Long id) {
		
		User user =userRepository.findById(id);
		
		if (user==null) {
			throw new ResourceNotFoundExeption(String.format(ExceptionMessage.USER_NOT_FOUND, id));
			
		}
		
		return user;
	}

	@Override
	public List<User> getAllUsers() {
		
		List<User> users=userRepository.findAll();
		if (users.isEmpty()) {
			throw new BadRequestExeption(ExceptionMessage.NO_USERS);
		}
		
		return users;
	}
	
	@Override
	public User updateUser(User user) {
		User upDatedUser=new User();
		upDatedUser.setUsername(user.getUsername());
		upDatedUser.setPassword(user.getPassword());
		userRepository.save(upDatedUser);
		return upDatedUser;
	}

	@Override
	public void deactivateUser(Long id) {
		
		userRepository.deleteById(id);
		
		
	}

	@Override
	public User login(String username, String password) {
		User user=userRepository.findByUserName(username);
		if (user==null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.USER_NOT_FOUND);
		}else if (user.getPassword().equalsIgnoreCase(password)) {
			throw new BadRequestExeption(ExceptionMessage.WRONG_PASSWORD);
		}else if (user.isActive()==false) {
			throw new BadRequestExeption(ExceptionMessage.USER_NOT_ACTIVE);
		}else
		return user;
	}

	

}
