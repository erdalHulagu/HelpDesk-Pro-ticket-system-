package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.repository.CommentRepository;

public class CommentDAO implements CommentRepository {

	private SessionFactory sessionFactory;

	public CommentDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void save(Comment comment) {

		Transaction transaction = null;

		try (Session session = sessionFactory.openSession()) {

			transaction = session.beginTransaction();

			if (comment.getId() == null) {
				session.persist(comment);
			} else {
				session.merge(comment);
			}

			transaction.commit();

		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		}
	}

	public Comment findById(Long id) {
	    try (Session session = sessionFactory.openSession()) {
	        return session.createQuery(
	                "FROM Comment c WHERE c.id = :id AND c.isDeleted = false",
	                Comment.class)
	            .setParameter("id", id)
	            .uniqueResult();
	    }
	}

	@Override
	public List<Comment> findAll() {

		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Comment c where c.isDeleted = false", Comment.class).list();
		}
	}

	@Override
	public void deleteById(Long id) {

		Transaction transaction = null;

		try (Session session = sessionFactory.openSession()) {

			transaction = session.beginTransaction();

			Comment comment = session.get(Comment.class, id);

			if (comment != null && !comment.isDeleted()) {
				comment.setDeleted(true);
			}

			transaction.commit();

		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		}
	}

	@Override
	public List<Comment> findAllByAuthorId(Long authorId) {

		Session session = sessionFactory.openSession();

		List<Comment> comments = session
				.createQuery("from Comment c where c.author.id = :authorId and c.isDeleted = false", Comment.class)
				.setParameter("authorId", authorId).list();

		session.close();

		return comments;
	}

	@Override
	public List<Comment> findAllByTicketId(Long ticketId) {
	    try (Session session = sessionFactory.openSession()) {
	        return session.createQuery(
	                "SELECT c FROM Comment c " +
	                "LEFT JOIN FETCH c.author " +
	                "LEFT JOIN FETCH c.ticket " +
	                "WHERE c.ticket.id = :ticketId " +
	                "AND c.isDeleted = false",
	                Comment.class
	        )
	        .setParameter("ticketId", ticketId)
	        .list();
	    }
	}

}
