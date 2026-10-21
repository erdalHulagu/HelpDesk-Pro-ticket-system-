
package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.AttachmentRepository;

public class AttachmentDAO implements AttachmentRepository {

    private final SessionFactory sessionFactory;

    public AttachmentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


@Override
public Attachment saveTicketAttachment(Attachment attachment) {

    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();

        if (attachment.getTicket() == null
                || attachment.getTicket().getId() == null) {
            throw new IllegalArgumentException(
                    "Ticket is required"
            );
        }

        Long ticketId = attachment.getTicket().getId();

        Ticket managedTicket =
                session.getReference(Ticket.class, ticketId);

        attachment.setTicket(managedTicket);
        attachment.setUser(null);

        session.persist(attachment);
        transaction.commit();

        return attachment;

    } catch (RuntimeException e) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (RuntimeException rollbackError) {
                e.addSuppressed(rollbackError);
            }
        }

        throw e;
    }
}

    @Override
    public Attachment saveUserAttachment(Attachment attachment) {

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                if (attachment.getUser() == null
                        || attachment.getUser().getId() == null) {
                    throw new IllegalArgumentException(
                            "User is required"
                    );
                }

                Long userId = attachment.getUser().getId();

                User managedUser =
                        session.getReference(User.class, userId);

                attachment.setUser(managedUser);
                attachment.setTicket(null);

                session.persist(attachment);
                transaction.commit();

                return attachment;

            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (RuntimeException rollbackError) {
                        e.addSuppressed(rollbackError);
                    }
                }

                throw e;
            }
        }
    }
    @Override
    public Attachment findById(Long id) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Attachment a where a.id = :id",
                    Attachment.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public List<Attachment> findByTicketId(Long ticketId) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Attachment a where a.ticket.id = :ticketId order by a.uploadedAt",
                    Attachment.class)
                    .setParameter("ticketId", ticketId)
                    .list();
        }
    }

    @Override
    public void deleteById(Long id) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Attachment attachment =
                    session.get(Attachment.class, id);

            if (attachment != null) {
                session.remove(attachment);
            }

            transaction.commit();

        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

	
}
