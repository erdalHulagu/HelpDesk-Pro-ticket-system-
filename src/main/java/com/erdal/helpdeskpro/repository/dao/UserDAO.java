package com.erdal.helpdeskpro.repository.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.User;
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

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        User existingUser = findByEmail(user.getEmail());

        if (existingUser != null) {
            transaction.rollback();
            session.close();
            throw new BadRequestExeption(ExceptionMessage.USER_ALREADY_EXIST);
        }

        session.persist(user);

        transaction.commit();
        session.close();
    }

    @Override
    public User findById(Long id) {

        Session session = sessionFactory.openSession();

        User user = session.get(User.class, id);

        session.close();

        return user;
    }

    @Override
    public List<User> findAll() {

        Session session = sessionFactory.openSession();

        List<User> users =
                session.createQuery("from User", User.class).list();

        List<User> newUsers = users.stream()
                .filter(User::isActive)
                .collect(Collectors.toList());

        session.close();

        return newUsers;
    }

    @Override
    public void deleteById(Long id) {

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        User user = session.get(User.class, id);

        if (user == null) {
            transaction.rollback();
            session.close();

            throw new BadRequestExeption(ExceptionMessage.USER_NOT_FOUND);
        }

        user.setActive(false);

        transaction.commit();
        session.close();
    }

    @Override
    public User findByEmail(String email) {

        String hql = "FROM User u WHERE u.email = :email";

        Session session = sessionFactory.openSession();

        User user = session.createQuery(hql, User.class)
                .setParameter("email", email)
                .uniqueResult();

        session.close();

        return user;
    }
}