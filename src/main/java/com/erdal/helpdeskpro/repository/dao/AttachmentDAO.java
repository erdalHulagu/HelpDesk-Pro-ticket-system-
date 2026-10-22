
package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AttachmentType;
import com.erdal.helpdeskpro.repository.AttachmentRepository;
import com.erdal.helpdeskpro.domain.Comment;

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
        attachment.setComment(null);

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
public Attachment saveCommentAttachment(Attachment attachment) {

    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();

        if (attachment.getComment() == null
                || attachment.getComment().getId() == null) {
            throw new IllegalArgumentException("Comment is required");
        }

        Long commentId = attachment.getComment().getId();

        Comment managedComment =
                session.getReference(Comment.class, commentId);

        attachment.setComment(managedComment);

        // Bu ek yalnızca yoruma bağlı olacak.
        attachment.setTicket(null);
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
                attachment.setComment(null);

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
    public Attachment findUserAttachment(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Attachment a " +
                    "WHERE a.user.id = :userId " +
                    "AND a.ticket IS NULL " +
                    "ORDER BY a.uploadedAt DESC",
                    Attachment.class
            )
            .setParameter("userId", userId)
            .setMaxResults(1)
            .uniqueResult();
        }
    }
    
    public Attachment findProfilePhoto(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Attachment a " +
                "WHERE a.user.id = :userId " +
                "AND a.attachmentType = :type " +
                "ORDER BY a.uploadedAt DESC",
                Attachment.class
            )
            .setParameter("userId", userId)
            .setParameter("type", AttachmentType.PROFILE_PHOTO)
            .setMaxResults(1)
            .uniqueResult();
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
    public List<Attachment> findByCommentId(Long commentId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Attachment a " +
                    "WHERE a.comment.id = :commentId " +
                    "ORDER BY a.uploadedAt ASC",
                    Attachment.class
            )
            .setParameter("commentId", commentId)
            .list();
        }
    }
    
    @Override
    public List<Attachment> findByTicketIdIncludingComments(Long ticketId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Attachment a " +
                    "WHERE a.ticket.id = :ticketId " +
                    "OR a.comment.ticket.id = :ticketId " +
                    "ORDER BY a.uploadedAt ASC",
                    Attachment.class
            )
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
