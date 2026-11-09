package com.erdal.helpdeskpro.config.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.domain.AnnouncementRead;
import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.AuthToken;
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.SecurityLog;
import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.domain.AssetMaintenance;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")

                    // Existing entities
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Ticket.class)
                    .addAnnotatedClass(Comment.class)
                    .addAnnotatedClass(Attachment.class)
                    .addAnnotatedClass(AuthToken.class)
                    .addAnnotatedClass(Notification.class)
                    .addAnnotatedClass(TicketActivity.class)
                    .addAnnotatedClass(Announcement.class)
                    .addAnnotatedClass(AnnouncementRead.class)
                    .addAnnotatedClass(SecurityLog.class)
                    .addAnnotatedClass(Asset.class)
                    .addAnnotatedClass(AssetAssignment.class)
                    .addAnnotatedClass(AssetRequest.class)
                    .addAnnotatedClass(AssetMaintenance.class)

                    .buildSessionFactory();

        } catch (Exception e) {
            throw new RuntimeException("SessionFactory oluşturulamadı", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}