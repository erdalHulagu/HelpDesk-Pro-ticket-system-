package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

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

	    User createdUser = new User();

	    createdUser.setUsername(user.getUsername());
	    createdUser.setEmail(user.getEmail());
	    createdUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
	    createdUser.setRole(Role.EMPLOYEE);
	    createdUser.setActive(true);

	    userRepository.save(createdUser);
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
	public List<User> getAllUsers(User currentUser) {
	    if (currentUser.getRole() != Role.ADMIN) {
	        throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
	    }

	    return userRepository.findAll();
	}
	
	@Override
	public User updateUser(User user) {
		User upDatedUser=userRepository.findById(user.getId());
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
	public User login(String email, String password) {

	    User user = userRepository.findByEmail(email);

	    if (user == null) {
	        throw new ResourceNotFoundExeption(ExceptionMessage.USER_NOT_FOUND);
	    }

	    if (!BCrypt.checkpw(password, user.getPassword())) {
	        throw new BadRequestExeption(ExceptionMessage.WRONG_PASSWORD);
	    }

	    if (!user.isActive()) {
	        throw new BadRequestExeption(ExceptionMessage.USER_NOT_ACTIVE);
	    }

	    return user;
	}

	@Override
	public List<User> findAllUsers() {
		
		return userRepository.findAll();
	}

	

}
