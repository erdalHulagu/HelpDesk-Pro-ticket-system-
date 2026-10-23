package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.service.UserService;

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;

	}

	@Override
	public void registerUser(User user) {

		User createdUser = new User();

		createdUser.setUsername(user.getUsername());
		createdUser.setEmail(user.getEmail());
		createdUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

		createdUser.setRole(Role.EMPLOYEE);
		createdUser.setActive(true);

		userRepository.save(createdUser);
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
	public User getUser(Long id) {

		User user = userRepository.findById(id);

		if (user == null) {
			throw new ResourceNotFoundExeption(String.format(ExceptionMessage.USER_NOT_FOUND, id));

		}

		return user;
	}

	@Override
	public List<User> getAllUsers(User currentUser) {
		if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.IT_MANAGER)) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		return userRepository.findAll();
	}

	@Override
	public User updateUser(User user) {

		if (user == null || user.getId() == null) {
			throw new IllegalArgumentException("User and user ID are required");
		}

		User existingUser = userRepository.findById(user.getId());

		if (existingUser == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.USER_NOT_FOUND);
		}

		if (user.getUsername() != null && !user.getUsername().isBlank()) {
			existingUser.setUsername(user.getUsername().trim());
		}

		if (user.getEmail() != null && !user.getEmail().isBlank()) {
			existingUser.setEmail(user.getEmail().trim());
		}

		if (user.getRole() != null) {
			existingUser.setRole(user.getRole());
		}

		if (user.getPassword() != null && !user.getPassword().isBlank()) {
			existingUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		}

		userRepository.save(existingUser);

		return existingUser;
	}

	@Override
	public void deactivateUser(User currentUser, Long id) {
		if (currentUser.getRole() != Role.ADMIN) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		userRepository.deleteById(id);

	}

}
