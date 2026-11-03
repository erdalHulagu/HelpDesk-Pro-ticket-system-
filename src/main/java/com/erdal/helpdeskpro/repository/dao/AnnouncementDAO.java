
package com.erdal.helpdeskpro.repository.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.repository.AnnouncementRepository;

public class AnnouncementDAO implements AnnouncementRepository {

    private final SessionFactory sessionFactory;

    public AnnouncementDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

  
    @Override
    public Announcement save(Announcement announcement) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            try {
                transaction = session.beginTransaction();

                Announcement savedAnnouncement;

                if (announcement.getId() == null) {
                    session.persist(announcement);
                    savedAnnouncement = announcement;
                } else {
                    savedAnnouncement = session.merge(announcement);
                }

                session.flush();
                transaction.commit();

                return savedAnnouncement;

            } catch (RuntimeException e) {

                // Rollback only if the transaction is still active.
                if (transaction != null && transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (RuntimeException rollbackException) {
                        e.addSuppressed(rollbackException);
                    }
                }

                // Preserve the original database exception.
                throw e;
            }
        }
    }
    
    @Override
    public Optional<Announcement> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Announcement announcement = session.createQuery(
                    "select a from Announcement a "
                    + "left join fetch a.createdBy "
                    + "where a.id = :id",
                    Announcement.class)
                    .setParameter("id", id)
                    .uniqueResult();

            return Optional.ofNullable(announcement);
        }
    }

    @Override
    public List<Announcement> findAllAnnouncements() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select a from Announcement a "
                    + "left join fetch a.createdBy "
                    + "order by a.pinned desc, a.createdAt desc",
                    Announcement.class)
                    .getResultList();
        }
    }

    @Override
    public List<Announcement> findActiveAnnouncementsByRole(Role role) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select a from Announcement a "
                    + "left join fetch a.createdBy "
                    + "where a.active = true "
                    + "and (a.expiresAt is null or a.expiresAt > :now) "
                    + "and (a.targetRole is null or a.targetRole = :role) "
                    + "order by a.pinned desc, a.createdAt desc",
                    Announcement.class)
                    .setParameter("now", LocalDateTime.now())
                    .setParameter("role", role)
                    .getResultList();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int deleted = session.createMutationQuery(
                    "delete from Announcement a "
                    + "where a.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            transaction.commit();

            return deleted > 0;

        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
