package com.erdal.helpdeskpro.http;

import org.hibernate.SessionFactory;

import com.erdal.helpdeskpro.config.HibernateUtil;
import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.repository.dao.CommentDAO;
import com.erdal.helpdeskpro.repository.dao.TicketDAO;
import com.erdal.helpdeskpro.repository.dao.UserDAO;
import com.erdal.helpdeskpro.service.CommentService;
import com.erdal.helpdeskpro.service.TicketService;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.service.impl.CommentServiceImpl;
import com.erdal.helpdeskpro.service.impl.TicketServiceImpl;
import com.erdal.helpdeskpro.service.impl.UserServiceImpl;

/**
 * Simple Dependency Injection container.
 * Creates and stores single instances (Singleton pattern).
 */
public class DependencyContainer {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    // Repositories
    private static final UserRepository userRepository =
            new UserDAO(sessionFactory);

    private static final TicketRepository ticketRepository =
            new TicketDAO(sessionFactory);

    private static final CommentRepository commentRepository =
            new CommentDAO(sessionFactory);

    // Services
    private static final UserService userService =
            new UserServiceImpl(userRepository);

    private static final TicketService ticketService =
            new TicketServiceImpl(ticketRepository);

    private static final CommentService commentService =
            new CommentServiceImpl(commentRepository, ticketRepository, userRepository);

    // Controllers
    private static final UserController userController =
            new UserController(userService);

    private static final TicketController ticketController =
            new TicketController(ticketService);

    private static final CommentController commentController =
            new CommentController(commentService);

    /**
     * Provide TicketController instance
     */
    public static TicketController getTicketController() {
        return ticketController;
    }

    /**
     * Provide UserController instance
     */
    public static UserController getUserController() {
        return userController;
    }

    /**
     * Provide CommentController instance
     */
    public static CommentController getCommentController() {
        return commentController;
    }
}
