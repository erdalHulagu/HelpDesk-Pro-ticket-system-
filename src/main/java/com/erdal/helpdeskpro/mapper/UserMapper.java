
package com.erdal.helpdeskpro.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.UserDTO;

public final class UserMapper {

    private UserMapper() {
    }

    public static User userDTOtoUser(UserDTO userDTO) {
        User user = new User();

        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());

        return user;
    }

    public static UserDTO userToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                null,
                user.getRole(),
                user.isActive()
        );
    }

    public static List<User> userDTOlistToUserList(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(UserMapper::userDTOtoUser)
                .collect(Collectors.toList());
    }

    public static List<UserDTO> userListToUserDTOList(List<User> users) {
        return users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        null,
                        null,
                        user.getRole(),
                        user.isActive()
                ))
                .collect(Collectors.toList());
    }
}
