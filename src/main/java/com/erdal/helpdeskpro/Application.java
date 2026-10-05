package com.erdal.helpdeskpro;

import java.net.InetSocketAddress;

import org.hibernate.SessionFactory;

import com.erdal.helpdeskpro.config.HibernateUtil;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.http.CommentHttpHandler;
import com.erdal.helpdeskpro.http.TicketHttpHandler;
import com.erdal.helpdeskpro.http.UserHttpHandler;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.repository.dao.UserDAO;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.service.impl.UserServiceImpl;
import com.sun.net.httpserver.HttpServer;

public class Application {

    public static void main(String[] args) throws Exception {

        // Hibernate
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        // User dependency chain
        UserRepository userRepository = new UserDAO(sessionFactory);

        UserService userService = new UserServiceImpl(userRepository);

        UserController userController = new UserController(userService);
        
       

        // HTTP Server
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext(
                "/users",
                new UserHttpHandler(userController)
        );

//        // Şimdilik bunları mevcut haliyle bırakıyoruz
//        server.createContext(
//                "/tickets",
//                new TicketHttpHandler(ticket)
//        );
//
//        server.createContext(
//                "/comments",
//                new CommentHttpHandler()
//        );

        server.setExecutor(null);

        server.start();

        System.out.println("🚀 Server started on http://localhost:8080");
    }
}