
package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Notification;
import com.erdal.helpdeskpro.repository.NotificationRepository;

public class NotificationDAO implements NotificationRepository {

    private final SessionFactory sessionFactory;

    public NotificationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Notification save(Notification notification) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.persist(notification);
            session.flush();

            transaction.commit();
            return notification;

        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<Notification> findAllByRecipientId(Long recipientId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select n from Notification n " +
                    "left join fetch n.actor " +
                    "left join fetch n.ticket " +
                    "where n.recipient.id = :recipientId " +
                    "order by n.createdAt desc",
                    Notification.class)
                .setParameter("recipientId", recipientId)
                .setMaxResults(100)
                .getResultList();
        }
    }

    @Override
    public long countUnreadByRecipientId(Long recipientId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select count(n) from Notification n " +
                    "where n.recipient.id = :recipientId " +
                    "and n.read = false",
                    Long.class)
                .setParameter("recipientId", recipientId)
                .getSingleResult();
        }
    }

    @Override
    public boolean markAsRead(Long notificationId, Long recipientId) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int updated = session.createMutationQuery(
                    "update Notification n set n.read = true " +
                    "where n.id = :notificationId " +
                    "and n.recipient.id = :recipientId")
                .setParameter("notificationId", notificationId)
                .setParameter("recipientId", recipientId)
                .executeUpdate();

            transaction.commit();
            return updated > 0;

        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public int markAllAsRead(Long recipientId) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int updated = session.createMutationQuery(
                    "update Notification n set n.read = true " +
                    "where n.recipient.id = :recipientId " +
                    "and n.read = false")
                .setParameter("recipientId", recipientId)
                .executeUpdate();

            transaction.commit();
            return updated;

        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
