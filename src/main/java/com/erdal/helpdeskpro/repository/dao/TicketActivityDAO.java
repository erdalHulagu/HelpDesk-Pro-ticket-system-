
package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.TicketActivityRepository;

public class TicketActivityDAO implements TicketActivityRepository {

    private SessionFactory sessionFactory;

    public TicketActivityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(TicketActivity activity) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Ticket managedTicket = session.get(
                Ticket.class,
                activity.getTicket().getId()
            );

            if (managedTicket == null) {
                throw new IllegalArgumentException("Ticket not found.");
            }

            User managedUser = session.get(
                User.class,
                activity.getPerformedBy().getId()
            );

            if (managedUser == null) {
                throw new IllegalArgumentException("User not found.");
            }

            activity.setTicket(managedTicket);
            activity.setPerformedBy(managedUser);

            session.persist(activity);
            transaction.commit();

        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }

            throw e;
        }
    }
    @Override
    public List<TicketActivity> findAllByTicketId(Long ticketId) {
        try (Session session = sessionFactory.openSession()) {
        	return session.createQuery(
        		    "SELECT a FROM TicketActivity a " +
        		    "JOIN FETCH a.performedBy " +
        		    "JOIN FETCH a.ticket " +
        		    "WHERE a.ticket.id = :ticketId " +
        		    "ORDER BY a.createdAt DESC",
        		    TicketActivity.class
        		).setParameter("ticketId", ticketId).list();
        }
    }
}
