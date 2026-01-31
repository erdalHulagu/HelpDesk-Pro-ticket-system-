package com.erdal.helpdeskpro.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.erdal.helpdeskpro.handler.UserHandler;
import com.sun.net.httpserver.HttpServer;

public class UserServer {

	
	public static void start() throws IOException {
		
		 HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

	        server.createContext("/register",  new UserHandler());
	        server.createContext("/users/{id}", new UserHandler());

	        server.start();
	        System.out.println("Server started on port 8080");
		
	}
}
