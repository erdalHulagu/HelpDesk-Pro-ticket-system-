
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
import com.erdal.helpdeskpro.http.TicketActivityHttpHandler;
import com.erdal.helpdeskpro.http.TicketExportHttpHandler;
import com.erdal.helpdeskpro.http.AnnouncementHttpHandler;
import com.erdal.helpdeskpro.http.SecurityLogHttpHandler;
import com.erdal.helpdeskpro.http.ExcelExportHttpHandler;
import com.erdal.helpdeskpro.http.UserExportHttpHandler;
import com.erdal.helpdeskpro.http.AssetAssignmentHttpHandler;
import com.erdal.helpdeskpro.http.AssetMaintenanceHttpHandler;

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

        // Users
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

        // Attachments
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

        // Ticket Activities
        server.createContext(
                "/activities",
                new TicketActivityHttpHandler(
                        DependencyContainer.getTicketActivityController(),
                        DependencyContainer.getAuthService()
                )
        );

        // Announcements
        server.createContext(
                "/announcements",
                new AnnouncementHttpHandler(
                        DependencyContainer.getAnnouncementController(),
                        DependencyContainer.getAnnouncementReadController(),
                        DependencyContainer.getAuthService()
                )
        );

        // Security Logs - ADMIN ONLY
        server.createContext(
                "/security-logs",
                new SecurityLogHttpHandler(
                        DependencyContainer.getSecurityLogService(),
                        DependencyContainer.getAuthService()
                )
        );
        
     // Exel 
        server.createContext(
        	    "/export/security-logs",
        	    new ExcelExportHttpHandler(
        	        DependencyContainer.getExcelExportService(),
        	        DependencyContainer.getSecurityLogService(),
        	        DependencyContainer.getAuthService()
        	    )
        	);
        
        server.createContext(
        	    "/export/users",
        	    new UserExportHttpHandler(
        	        DependencyContainer.getExcelExportService(),
        	        DependencyContainer.getUserService(),
        	        DependencyContainer.getAuthService()
        	    )
        	);
        
        server.createContext(
        	    "/export/tickets",
        	    new TicketExportHttpHandler(
        	        DependencyContainer.getExcelExportService(),
        	        DependencyContainer.getTicketService(),
        	        DependencyContainer.getAuthService()
        	    )
        	);
        
     // Asset Assignment
        server.createContext(
                "/asset-assignments",
                new AssetAssignmentHttpHandler(
                        DependencyContainer.getAssetAssignmentController(),
                        DependencyContainer.getAuthService()
                )
        );
        
     // Asset Maintenance
        server.createContext(
                "/asset-maintenance",
                new AssetMaintenanceHttpHandler(
                        DependencyContainer.getAssetMaintenanceController(),
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
