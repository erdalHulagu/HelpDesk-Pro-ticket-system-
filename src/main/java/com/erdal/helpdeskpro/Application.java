
package com.erdal.helpdeskpro;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.erdal.helpdeskpro.config.DependencyContainer;
import com.erdal.helpdeskpro.http.AttachmentHttpHandler;
import com.erdal.helpdeskpro.http.AuthHttpHandler;
import com.erdal.helpdeskpro.http.CommentHttpHandler;
import com.erdal.helpdeskpro.http.TicketHttpHandler;
import com.erdal.helpdeskpro.http.UserHttpHandler;
import com.erdal.helpdeskpro.http.NotificationHttpHandler;
public class Application {

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(new InetSocketAddress(8080), 0);

     // Authentication
        server.createContext(
                "/auth",
                new AuthHttpHandler(
                        DependencyContainer.getAuthController(),
                        DependencyContainer.getAuthService()
                )
        );

        server.createContext(
        	    "/users",
        	    new UserHttpHandler(
        	        DependencyContainer.getUserController(),
        	        DependencyContainer.getAuthService(),
        	        DependencyContainer.getAuthorization()
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
        
        // attachments
        server.createContext(
                "/attachments",
                new AttachmentHttpHandler(
                        DependencyContainer.getAttachmentController(),
                        DependencyContainer.getAuthService()
                )
        );
        
     // Notifications
        server.createContext(
                "/notifications",
                new NotificationHttpHandler(
                        DependencyContainer.getNotificationController(),
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
