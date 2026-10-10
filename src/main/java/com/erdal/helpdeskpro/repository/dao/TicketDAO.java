package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.TicketRepository;

public class TicketDAO implements TicketRepository {

	private SessionFactory sessionFactory;

	public TicketDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	@Override
	public void save(Ticket ticket) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();

		if (ticket.getCreatedBy() != null) {
			User user = session.getReference(User.class, ticket.getCreatedBy().getId());

			ticket.setCreatedBy(user);
		}

		session.persist(ticket);// hibernate (persist) save icin kullanilir
		transaction.commit();

		session.close();
	}

	@Override
	public Ticket findById(Long id) {
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Ticket t where t.id = :id and t.isDeleted = false", Ticket.class)
					.setParameter("id", id).uniqueResult();
		}
	}

	@Override
	public List<Ticket> findAll() {
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Ticket t where t.isDeleted = false", Ticket.class).list();
		}
	}

	@Override
	public void update(Ticket ticket) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();

			try {
				session.merge(ticket); // hibernate (merge) update icin kullanilir
				transaction.commit();
			} catch (RuntimeException e) {
				if (transaction.isActive()) {
					transaction.rollback();
				}
				throw e;
			}
		}
	}

	@Override
	public void deleteById(Long id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Ticket ticket = session.get(Ticket.class, id);
		if (ticket != null) {
			ticket.setDeleted(true);
		}

		tx.commit();
		session.close();

	}

	@Override
	public List<Ticket> findAllIncludingDeleted() {
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Ticket", Ticket.class).list();
		}
	}
	
	@Override
	public Ticket findByIdIncludingDeleted(Long id) {
	    try (Session session = sessionFactory.openSession()) {
	        return session.createQuery(
	                "from Ticket t where t.id = :id",
	                Ticket.class
	        )
	        .setParameter("id", id)
	        .uniqueResult();
	    }
	}
	
	@Override
	public List<Ticket> findAllActiveByCreatedBy(Long userId) {

	    try (Session session = sessionFactory.openSession()) {
	        return session.createQuery(
	                "from Ticket t where t.isDeleted = false and t.createdBy.id = :userId",
	                Ticket.class
	        )
	        .setParameter("userId", userId)
	        .list();
	    }
	}
	
	

	@Override
	public List<Ticket> findAllActive() {

		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Ticket t where t.isDeleted = false", Ticket.class).list();
		}
	}
	
	@Override
	public List<Ticket> findByCreatedBy(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	


}
