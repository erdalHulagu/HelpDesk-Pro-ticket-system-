
package com.erdal.helpdeskpro.repository.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.AuthToken;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.AuthTokenRepository;

public class AuthTokenDAO implements AuthTokenRepository {

    private final SessionFactory sessionFactory;

    public AuthTokenDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(AuthToken token) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User managedUser = session.getReference(
                User.class,
                token.getUser().getId()
            );

            token.setUser(managedUser);
            session.persist(token);

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public AuthToken findByTokenHash(String tokenHash) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "select t from AuthToken t " +
                "join fetch t.user " +
                "where t.tokenHash = :tokenHash",
                AuthToken.class
            )
            .setParameter("tokenHash", tokenHash)
            .uniqueResult();
        }
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.createMutationQuery(
                "update AuthToken t " +
                "set t.revoked = true " +
                "where t.tokenHash = :tokenHash and t.revoked = false"
            )
            .setParameter("tokenHash", tokenHash)
            .executeUpdate();

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
