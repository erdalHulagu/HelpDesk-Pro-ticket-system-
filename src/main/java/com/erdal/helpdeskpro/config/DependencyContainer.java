
package com.erdal.helpdeskpro.config;

import org.hibernate.SessionFactory;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.AuthServiceImpl;
import com.erdal.helpdeskpro.auth.TokenService;
import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.authorization.AuthorizationImpl;
import com.erdal.helpdeskpro.config.util.HibernateUtil;
import com.erdal.helpdeskpro.controller.AuthController;
import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.repository.AuthTokenRepository;
import com.erdal.helpdeskpro.repository.dao.CommentDAO;
import com.erdal.helpdeskpro.repository.dao.TicketDAO;
import com.erdal.helpdeskpro.repository.dao.UserDAO;
import com.erdal.helpdeskpro.repository.dao.AuthTokenDAO;
import com.erdal.helpdeskpro.service.CommentService;
import com.erdal.helpdeskpro.service.TicketService;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.service.impl.CommentServiceImpl;
import com.erdal.helpdeskpro.service.impl.TicketServiceImpl;
import com.erdal.helpdeskpro.service.impl.UserServiceImpl;
import com.erdal.helpdeskpro.controller.AttachmentController;
import com.erdal.helpdeskpro.repository.AttachmentRepository;
import com.erdal.helpdeskpro.repository.dao.AttachmentDAO;
import com.erdal.helpdeskpro.service.AttachmentService;
import com.erdal.helpdeskpro.service.impl.AttachmentServiceImpl;

public final class DependencyContainer {

	private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	// Repositories
	private static final UserRepository userRepository = new UserDAO(sessionFactory);

	private static final TicketRepository ticketRepository = new TicketDAO(sessionFactory);

	private static final CommentRepository commentRepository = new CommentDAO(sessionFactory);

	// Authorization
	private static final Authorization authorization = new AuthorizationImpl();

	// Services
	private static final UserService userService = new UserServiceImpl(userRepository);

	private static final TicketService ticketService = new TicketServiceImpl(ticketRepository, authorization);

	private static final CommentService commentService = new CommentServiceImpl(commentRepository, ticketRepository,
			userRepository, authorization);
	// Token repository
	private static final AuthTokenRepository authTokenRepository = new AuthTokenDAO(sessionFactory);

	// Authentication
	private static final TokenService tokenService = new TokenService(authTokenRepository, userRepository);

	private static final AuthService authService = new AuthServiceImpl(userService, tokenService);

	// Controllers
	private static final UserController userController = new UserController(userService);

	private static final TicketController ticketController = new TicketController(ticketService);

	private static final CommentController commentController = new CommentController(commentService);

	private static final AuthController authController = new AuthController(authService);

	// Attachments
	private static final AttachmentRepository attachmentRepository = new AttachmentDAO(sessionFactory);

	private static final AttachmentService attachmentService = new AttachmentServiceImpl(attachmentRepository,
			ticketRepository, authorization, commentRepository);

	private static final AttachmentController attachmentController = new AttachmentController(attachmentService);

	private DependencyContainer() {
		// Bu sınıftan nesne oluşturulmasını engeller.
	}

	public static UserController getUserController() {
		return userController;
	}

	public static TicketController getTicketController() {
		return ticketController;
	}

	public static CommentController getCommentController() {
		return commentController;
	}

	public static AuthController getAuthController() {
		return authController;
	}

	public static AuthService getAuthService() {
		return authService;
	}

	public static UserRepository getUserRepository() {
		return userRepository;
	}

	public static AttachmentController getAttachmentController() {
		return attachmentController;
	}

	public static Authorization getAuthorization() {
		
		return authorization;
	}
}
