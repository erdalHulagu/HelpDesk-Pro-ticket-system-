package com.erdal.helpdeskpro;

	import java.net.InetSocketAddress;

import com.erdal.helpdeskpro.http.CommentHttpHandler;
import com.erdal.helpdeskpro.http.TicketHttpHandler;
import com.erdal.helpdeskpro.http.UserHttpHandler;
import com.sun.net.httpserver.HttpServer;

	/**
	 * Main class responsible for starting the embedded HTTP server.
	 * This replaces Spring Boot's auto configuration.
	 */
	public class Application {

	    public static void main(String[] args) throws Exception {

	        // Create HTTP server on port 8080
	        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

	        /**
	         * Register endpoint contexts.
	         * Each context acts like a REST controller mapping.
	         */
	        server.createContext("/tickets", new TicketHttpHandler());
	        server.createContext("/users", new UserHttpHandler());
	        server.createContext("/comments", new CommentHttpHandler());

	        // Default executor (creates a thread pool automatically)
	        server.setExecutor(null);

	        // Start server
	        server.start();

	        System.out.println("🚀 Server started on http://localhost:8080");
	    }
	}
//	Ne Yaptık (kısa mantık)
//	• Java’nın built-in HTTP server’ını başlattık
//	• Port → 8080
//	• Endpoint mapping yaptık
//	/tickets   → TicketHttpHandler
//	/users     → UserHttpHandler
//	/comments  → CommentHttpHandler
//	Yani artık uygulaman gerçek REST server gibi çalışacak
