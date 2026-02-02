package com.erdal.helpdeskpro;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.erdal.helpdeskpro.config.DatabaseConnect;
import com.erdal.helpdeskpro.handler.UserHandler;
import com.erdal.helpdeskpro.server.UserServer;
import com.erdal.helpdeskpro.service.UserService;

public class Application {

	
	public static void main(String[] args) throws IOException  {
		
//		DatabaseConnect.initializeDatabase(); 
        UserServer.start();
        
    }
//	public static void main(String[] args) throws Exception {
//		
//		DatabaseConnect.initializeDatabase(); 
//		
//		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//		
//		server.createContext("/register",  new UserHandler());
//		
//		server.start();
//		System.out.println("Server started on port 8080");
//	}
}
