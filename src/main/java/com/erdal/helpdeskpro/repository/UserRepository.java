package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.User;

public interface UserRepository {

  void save(User user); 
  User findById(Long id);
  List<User> findAll();
  void deleteById(Long id);
  User findByUserName(String name);
  


}
