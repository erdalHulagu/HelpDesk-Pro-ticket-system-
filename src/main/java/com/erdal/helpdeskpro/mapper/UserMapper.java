package com.erdal.helpdeskpro.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;


//Since I’m not using any mapping framework, I implemented manual mapper classes to keep controllers clean.
public final class UserMapper {

	public static User userDTOtoUser(UserDTO userDTO) {

		return new User(userDTO.getId(), 
		                userDTO.getUsername(), 
		                userDTO.getEmail(), 
		                userDTO.getRole(),
		                userDTO.isActive());
	

	}

	public static UserDTO userToUserDTO(User user) {

		return new UserDTO(user.getId(), 
				           user.getUsername(), 
				           user.getEmail(), 
				           user.getPassword(),
				           user.getRole(),
				           user.isActive());

	}
	

	public static List<User> userDTOlistToUserList(List<UserDTO> userDTOs) {

		return userDTOs.stream().map(u -> new User(u.getId(), 
				                                   u.getUsername(),
				                                   u.getRole(),
				                                   u.getEmail(),
				                                   u.isActive()))
				.collect(Collectors.toList());

	}
	
	
	public static List<UserDTO> userListToUserDTOList(List<User> users) {
		
		return users.stream().map(user -> new UserDTO(user.getId(), 
		                                              user.getUsername(), 
		                                              user.getEmail(),
		                                              user.getPassword(),
		                                              user.getRole(),
		                                              user.isActive())).collect(Collectors.toList());
		
	}

}
