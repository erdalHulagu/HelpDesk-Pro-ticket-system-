package com.erdal.helpdeskpro.repository.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.domain.AnnouncementRead;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.AnnouncementReadRepository;

public class AnnouncementReadDAO implements AnnouncementReadRepository {

    private final SessionFactory sessionFactory;

    public AnnouncementReadDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void markAsRead(Long announcementId, Long userId) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            Long count = session.createQuery(
                    "select count(ar.id) from AnnouncementRead ar "
                    + "where ar.announcement.id = :announcementId "
                    + "and ar.user.id = :userId",
                    Long.class)
                    .setParameter("announcementId", announcementId)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (count == null || count == 0) {

                Announcement announcement = session.get(
                        Announcement.class, announcementId);

                User user = session.get(User.class, userId);

                if (announcement == null || user == null) {
                    throw new IllegalArgumentException(
                            "Announcement or user not found.");
                }

                AnnouncementRead read =
                        new AnnouncementRead(announcement, user);

                read.setReadAt(LocalDateTime.now());

                session.persist(read);
            }

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public boolean isRead(Long announcementId, Long userId) {

        try (Session session = sessionFactory.openSession()) {

            Long count = session.createQuery(
                    "select count(ar.id) from AnnouncementRead ar "
                    + "where ar.announcement.id = :announcementId "
                    + "and ar.user.id = :userId",
                    Long.class)
                    .setParameter("announcementId", announcementId)
                    .setParameter("userId", userId)
                    .uniqueResult();

            return count != null && count > 0;
        }
    }

    @Override
    public List<Long> getReadAnnouncementIds(Long userId) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "select ar.announcement.id from AnnouncementRead ar "
                    + "where ar.user.id = :userId",
                    Long.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }
   
    @Override
    public LocalDateTime getReadAt(Long announcementId, Long userId) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "select ar.readAt from AnnouncementRead ar "
                    + "where ar.announcement.id = :announcementId "
                    + "and ar.user.id = :userId",
                    LocalDateTime.class)
                    .setParameter("announcementId", announcementId)
                    .setParameter("userId", userId)
                    .uniqueResult();
        }
    }
    
    
    @Override
    public void markAllAsRead(List<Long> announcementIds, Long userId) {

        if (announcementIds == null || announcementIds.isEmpty()) {
            return;
        }

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            User user = session.get(User.class, userId);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            for (Long announcementId : announcementIds) {

                if (announcementId == null) {
                    continue;
                }

                Long count = session.createQuery(
                        "SELECT COUNT(ar.id) FROM AnnouncementRead ar " +
                        "WHERE ar.announcement.id = :announcementId " +
                        "AND ar.user.id = :userId",
                        Long.class
                )
                .setParameter("announcementId", announcementId)
                .setParameter("userId", userId)
                .uniqueResult();

                if (count != null && count == 0) {

                    Announcement announcement =
                            session.get(Announcement.class, announcementId);

                    if (announcement != null) {
                        AnnouncementRead announcementRead =
                                new AnnouncementRead(announcement, user);

                        session.persist(announcementRead);
                    }
                }
            }

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Failed to mark all announcements as read",
                    e
            );
        }
    }
}
