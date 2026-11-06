package com.erdal.helpdeskpro.repository.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.repository.UserRepository;

public class UserDAO implements UserRepository {

    private SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(User user) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            User existingUser = session.createQuery(
                    "FROM User u WHERE u.email = :email",
                    User.class
            )
            .setParameter("email", user.getEmail())
            .uniqueResult();

            // Prevent duplicate email addresses.
            if (existingUser != null &&
                !existingUser.getId().equals(user.getId())) {

                throw new BadRequestExeption(
                    ExceptionMessage.USER_ALREADY_EXIST
                );
            }

            if (user.getId() == null) {
                session.persist(user);
            } else {
                session.merge(user);
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
    public User findById(Long id) {

        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public List<User> findAll() {

        try (Session session = sessionFactory.openSession()) {

            // Return ALL users, including inactive accounts.
            return session.createQuery(
                "FROM User u ORDER BY u.id DESC",
                User.class
            ).list();
        }
    }

    @Override
    public void deleteById(Long id) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            User user = session.get(User.class, id);

            if (user == null) {
                throw new BadRequestExeption(
                    ExceptionMessage.USER_NOT_FOUND
                );
            }

            // Soft delete: deactivate account instead of deleting it.
            user.setActive(false);

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {

        String hql = "FROM User u WHERE u.email = :email";

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(hql, User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public List<User> findAllITSupportUsers() {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                "FROM User u WHERE u.role = :role AND u.isActive = true",
                User.class
            )
            .setParameter("role", Role.IT_SUPPORT)
            .list();
        }
    }

    @Override
    public List<User> findAllByCreatedAtRange(
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {

        try (Session session = sessionFactory.openSession()) {

            // Include both active and inactive users in exports.
            StringBuilder hql = new StringBuilder(
                "FROM User u WHERE 1=1"
            );

            if (startDate != null) {
                hql.append(" AND u.createdAt >= :startDate");
            }

            if (endDate != null) {
                hql.append(" AND u.createdAt <= :endDate");
            }

            hql.append(" ORDER BY u.createdAt DESC");

            var query = session.createQuery(
                hql.toString(),
                User.class
            );

            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }

            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }

            return query.list();
        }
    }
}
