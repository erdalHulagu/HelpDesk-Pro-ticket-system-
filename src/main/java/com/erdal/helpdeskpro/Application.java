
package com.erdal.helpdeskpro;

import java.net.InetSocketAddress;

import org.hibernate.SessionFactory;

import com.sun.net.httpserver.HttpServer;

import com.erdal.helpdeskpro.auth.AuthService;
import com.erdal.helpdeskpro.auth.AuthServiceImpl;
import com.erdal.helpdeskpro.auth.TokenService;
import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.authorization.AuthorizationImpl;
import com.erdal.helpdeskpro.config.HibernateUtil;
import com.erdal.helpdeskpro.controller.AuthController;
import com.erdal.helpdeskpro.controller.TicketController;
import com.erdal.helpdeskpro.controller.UserController;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.http.AuthHttpHandler;
import com.erdal.helpdeskpro.http.TicketHttpHandler;
import com.erdal.helpdeskpro.http.UserHttpHandler;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.repository.dao.TicketDAO;
import com.erdal.helpdeskpro.repository.dao.UserDAO;
import com.erdal.helpdeskpro.service.TicketService;
import com.erdal.helpdeskpro.service.UserService;
import com.erdal.helpdeskpro.service.impl.TicketServiceImpl;
import com.erdal.helpdeskpro.service.impl.UserServiceImpl;
import com.erdal.helpdeskpro.controller.CommentController;
import com.erdal.helpdeskpro.http.CommentHttpHandler;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.dao.CommentDAO;
import com.erdal.helpdeskpro.service.CommentService;
import com.erdal.helpdeskpro.service.impl.CommentServiceImpl;

public class Application {

	public static void main(String[] args) throws Exception {

		// Hibernate bağlantısını al.
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

		// ----------------------------------------------
		// User katmanlarını oluştur.
		// ----------------------------------------------
		UserRepository userRepository = new UserDAO(sessionFactory);

		UserService userService = new UserServiceImpl(userRepository);

		UserController userController = new UserController(userService);

		// ----------------------------------------------
		// Ticket katmanlarını oluştur.
		// ----------------------------------------------

		TicketRepository ticketRepository = new TicketDAO(sessionFactory);

		Authorization authorization = new AuthorizationImpl();

		TicketService ticketService = new TicketServiceImpl(ticketRepository, authorization);

		TicketController ticketController = new TicketController(ticketService);

		// ----------------------------------------------
		// Comment katmanlarıni oluştur.
		// ----------------------------------------------
		CommentRepository commentRepository = new CommentDAO(sessionFactory);

		CommentService commentService = new CommentServiceImpl(commentRepository, ticketRepository, userRepository,
				authorization);

		CommentController commentController = new CommentController(commentService);

		// ----------------------------------------------
		// Auth için tek bir TokenService oluştur.
		// ----------------------------------------------

		TokenService tokenService = new TokenService();

		AuthService authService = new AuthServiceImpl(userService, tokenService);

		AuthController authController = new AuthController(authService);

		// Geçici ticket testi kullanıcısı.
		User authenticatedUser = userRepository.findById(7L);

		// HTTP sunucusunu oluştur.
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

		// Kayıt ve giriş endpoint'leri.
		server.createContext("/auth", new AuthHttpHandler(authController));

		// Kullanıcı endpoint'leri.
		server.createContext("/users", new UserHttpHandler(userController));

		// Ticket endpoint'leri.
		// Her istekte kullanıcıyı token üzerinden bulur.
		server.createContext("/tickets", new TicketHttpHandler(ticketController, authService));

		server.createContext("/admin/tickets", new TicketHttpHandler(ticketController, authService));

		server.createContext("/comments", new CommentHttpHandler(commentController, authService));
		
		
		// HTTP sunucusunu başlat.
		server.setExecutor(null);
		server.start();

		System.out.println("Server started on http://localhost:8080");
	}
}
