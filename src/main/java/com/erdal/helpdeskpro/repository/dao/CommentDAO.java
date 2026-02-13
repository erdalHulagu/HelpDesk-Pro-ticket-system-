package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.repository.CommentRepository;

public class CommentDAO implements CommentRepository {
	
	
	private SessionFactory sessionFactory;
	
	public CommentDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}
	
	private Session session=sessionFactory.openSession();

	@Override
	public void save(Comment comment) {
//		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		
		session.persist(comment);
		transaction.commit();
		
		session.close();
		
	}

	@Override
	public Comment findById(Long id) {
//		Session session =sessionFactory.openSession();
		Comment comment=session.get(Comment.class, id);
		
		session.close();
		
		return comment;
	}

	@Override
	public List<Comment> findAll() {
//		Session session=sessionFactory.openSession();
		List<Comment>comments=session.createQuery("from Comment",Comment.class).list();
		
		session.close();
		
		return comments;
	}

	@Override
	public void deleteById(Long id) {
//		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		Comment comment = session.get(Comment.class, id);
		if (comment != null && !comment.isDeleted()) {
		    comment.setDeleted(true);
		}
		transaction.commit();
		session.close();
		
	}
	@Override
	public List<Comment> findAllByAuthorId(Long authorId) {

//	    Session session = sessionFactory.openSession();

	    List<Comment> comments = session.createQuery(
	            "from Comment c where c.author.id = :authorId and c.deleted = false",
	            Comment.class)
	            .setParameter("authorId", authorId)
	            .list();

	    session.close();

	    return comments;
	}
	@Override
	public List<Comment> findAllByTicketId(Long ticketId){
//		Session session=sessionFactory.openSession();
		
		
		List<Comment> comments =session.createQuery(
				"form Comment c where c.ticket.id :=ticketId and c.ticket.isDeleted = false",Comment.class)
				.setParameter("ticketId", ticketId)
				.list();
		session.close();
		return comments;
	}

}
