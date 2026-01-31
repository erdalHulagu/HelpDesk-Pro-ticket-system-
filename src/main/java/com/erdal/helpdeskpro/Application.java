package com.erdal.helpdeskpro;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.erdal.helpdeskpro.config.DatabaseConnect;
import com.erdal.helpdeskpro.handler.UserHandler;

public class Application {

	
	public static void main(String[] args)  {
		
		DatabaseConnect.initializeDatabase(); 

        
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
