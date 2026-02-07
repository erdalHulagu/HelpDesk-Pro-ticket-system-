package com.erdal.helpdeskpro.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.repository.CommonRepository;

public class CommentDAO implements CommonRepository {
	
	
	SessionFactory sessionFactory;
	
	public CommentDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}

	@Override
	public void save(Comment comment) {
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		
		session.persist(comment);
		transaction.commit();
		
		session.close();
		
	}

	@Override
	public Comment findById(Long id) {
		Session session =sessionFactory.openSession();
		Comment comment=session.get(Comment.class, id);
		
		session.close();
		
		return comment;
	}

	@Override
	public List<Comment> findAll() {
		Session session=sessionFactory.openSession();
		List<Comment>comments=session.createQuery("from Comment",Comment.class).list();
		
		session.close();
		
		return comments;
	}

	@Override
	public void delete(Long id) {
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		Comment comment = session.get(Comment.class, id);
		if (comment != null && !comment.isDeleted()) {
		    comment.setDeleted(true);
		}
		transaction.commit();
		session.close();
		
	}

}
