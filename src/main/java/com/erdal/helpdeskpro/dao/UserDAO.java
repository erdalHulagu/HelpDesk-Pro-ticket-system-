package com.erdal.helpdeskpro.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.UserRepository;

public class UserDAO implements UserRepository {
	
	private SessionFactory sessionFactory;
	
	public UserDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}

	@Override
	public void save(User user) {
		
		Session session =sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		session.persist(user);
		transaction.commit();
		session.close();
		
		
	}

	@Override
	public User findById(Long id) {
		Session session=sessionFactory.openSession();
		User user =session.get(User.class, id);
		
		session.close();
		
		return user;
	}

	@Override
	public List<User> findAll() {
		Session session=sessionFactory.openSession();
		List<User>users=session.createQuery("from User",User.class).list();
		
		session.close();
		
		
		return users;
	}

	@Override
	public void deleteById(Long id) {
		Session session =sessionFactory.openSession();
		User user=session.get(User.class, id);
		Transaction transaction =session.beginTransaction();
		
		if (user!=null) {
			user.setActive(false);
			
		}
		transaction.commit();
		session.close();
		
		
	}

	@Override
	public User findByUserName(String username) {
		Session session=sessionFactory.openSession();
		User user=session.get(User.class, username);
		
		session.close();
		return user;
	}
	
	

}
