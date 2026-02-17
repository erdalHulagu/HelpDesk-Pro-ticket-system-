package com.erdal.helpdeskpro;

import com.erdal.helpdeskpro.config.HibernateUtil;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.service.impl.UserServiceImpl;
import com.erdal.helpdeskpro.dtos.UserDTO;
import com.erdal.helpdeskpro.exception.GlobalExceptionHandler;
import com.erdal.helpdeskpro.repository.dao.UserDAO;

public class Application {

	public static void main(String[] args) {

        try {

            UserService userService = new UserServiceImpl(new UserDAO(HibernateUtil.getSessionFactory()));
            UserController userController = new UserController(userService);

            UserDTO user = new UserDTO();
            user.setUsername("erdal");
            user.setPassword("1234");
            user.setEmail("erdal@mail.com");

            userController.register(user);

            System.out.println(" User created");

        } catch (Exception e) {
            GlobalExceptionHandler.handle(e);
        }
    }

}
