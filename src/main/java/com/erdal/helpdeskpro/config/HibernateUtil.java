package com.erdal.helpdeskpro.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Ticket.class)
                    .addAnnotatedClass(Comment.class)
                    .addAnnotatedClass(Attachment.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("SessionFactory oluşturulamadı", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
};