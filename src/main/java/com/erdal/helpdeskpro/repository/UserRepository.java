package com.erdal.helpdeskpro.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.User;

public interface UserRepository {

	void save(User user);

	User findById(Long id);

	List<User> findAll();

	void deleteById(Long id);

	User findByEmail(String email);

	List<User> findAllITSupportUsers();

	List<User> findAllByCreatedAtRange(LocalDateTime startDate, LocalDateTime endDate);

}
