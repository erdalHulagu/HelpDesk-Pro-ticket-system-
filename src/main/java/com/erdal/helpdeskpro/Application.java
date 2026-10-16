
package com.erdal.helpdeskpro;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.erdal.helpdeskpro.config.DependencyContainer;
import com.erdal.helpdeskpro.http.AuthHttpHandler;
import com.erdal.helpdeskpro.http.CommentHttpHandler;
import com.erdal.helpdeskpro.http.TicketHttpHandler;
import com.erdal.helpdeskpro.http.UserHttpHandler;

public class Application {

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(new InetSocketAddress(8080), 0);

        // Authentication
        server.createContext(
                "/auth",
                new AuthHttpHandler(
                        DependencyContainer.getAuthController()
                )
        );

        server.createContext(
                "/users",
                new UserHttpHandler(
                        DependencyContainer.getUserController(),
                        DependencyContainer.getAuthService()
                )
        );

        // Tickets
        server.createContext(
                "/tickets",
                new TicketHttpHandler(
                        DependencyContainer.getTicketController(),
                        DependencyContainer.getAuthService()
                )
        );

        server.createContext(
                "/admin/tickets",
                new TicketHttpHandler(
                        DependencyContainer.getTicketController(),
                        DependencyContainer.getAuthService()
                )
        );

        // Comments
        server.createContext(
                "/comments",
                new CommentHttpHandler(
                        DependencyContainer.getCommentController(),
                        DependencyContainer.getAuthService()
                )
        );

        server.setExecutor(null);
        server.start();

        System.out.println(
                "Server started on http://localhost:8080"
        );
    }
}
