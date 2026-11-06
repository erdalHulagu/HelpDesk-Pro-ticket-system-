package com.erdal.helpdeskpro.service;

import java.time.LocalDateTime;
import java.util.List;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.web.request.RegisterRequest;

public interface UserService {

	User getUser(Long id);

	void deactivateUser(User currentUser, Long id);

	User login(String email, String password);

	User updateUser(User user);

	List<User> getAllUsers(User currentUser);

	void registerUser(User user);

	List<UserDTO> getAllITSupportUsers(User currentUser);

	List<User> getUsersForExport(User currentUser, LocalDateTime startDate, LocalDateTime endDate);

}
