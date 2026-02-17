package com.erdal.helpdeskpro.repository.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
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
		
		User usr=findByUserName(user.getUsername());
		if (usr==null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.USER_IS_NULL);
			
		}
		if (usr.getUsername().equalsIgnoreCase(user.getUsername())) {
			throw new BadRequestExeption(ExceptionMessage.USER_ALREADY_EXIST);
			
		}
		
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
		
		List<User> newusers=users.stream().filter(u->u.isActive()==true).collect(Collectors.toList());
		
		session.close();
		
		
		return newusers;
	}

	@Override
	public void deleteById(Long id) {
		Session session =sessionFactory.openSession();
		User user=session.get(User.class, id);
		Transaction transaction =session.beginTransaction();
		
		if (user==null) {
			throw new BadRequestExeption(ExceptionMessage.USER_NOT_FOUND);
			
		}
		user.setActive(false);
		transaction.commit();
		session.close();
		
		
	}

	@Override
	public User findByUserName(String username) {
		
		String hql="From User u WHERE u.username = :username";
		Session session=sessionFactory.openSession();
		User user= session.createQuery(hql,User.class).setParameter("username", username).uniqueResult();

		if (user==null) {
			return user;
			
		}
		session.close();
		return user;
	}
	
	

}
