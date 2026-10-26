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
	    Transaction transaction = null;

	    try (Session session = sessionFactory.openSession()) {
	        transaction = session.beginTransaction();

	        if (ticket.getCreatedBy() != null) {
	            User managedUser = session.get(
	                User.class,
	                ticket.getCreatedBy().getId()
	            );

	            if (managedUser == null) {
	                throw new IllegalArgumentException(
	                    "Ticket creator not found."
	                );
	            }

	            ticket.setCreatedBy(managedUser);
	        }

	        session.persist(ticket);
	        transaction.commit();

	    } catch (RuntimeException e) {
	        if (transaction != null && transaction.isActive()) {
	            transaction.rollback();
	        }
	        throw e;
	    }
	}
//	@Override
//	public Ticket findById(Long id) {
//		try (Session session = sessionFactory.openSession()) {
//			return session.createQuery("from Ticket t where t.id = :id and t.isDeleted = false", Ticket.class)
//					.setParameter("id", id).uniqueResult();
//		}
//	}
	@Override
	public Ticket findById(Long id) {
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Ticket t where t.id = :id", Ticket.class).setParameter("id", id)
					.uniqueResult();
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
			return session.createQuery("from Ticket t where t.id = :id", Ticket.class).setParameter("id", id)
					.uniqueResult();
		}
	}

	@Override
	public List<Ticket> findAllActiveByCreatedBy(Long userId) {
		try (Session session = sessionFactory.openSession()) {
			return session
					.createQuery("select t from Ticket t " + "left join fetch t.createdBy "
							+ "where t.isDeleted = false " + "and t.createdBy.id = :userId", Ticket.class)
					.setParameter("userId", userId).list();
		}
	}

	@Override
	public List<Ticket> findAllActive() {

		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from Ticket t where t.isDeleted = false", Ticket.class).list();
		}
	}

	@Override
	public List<Ticket> findAllActiveByAssignedTo(Long userId) {
		try (Session session = sessionFactory.openSession()) {
			return session
					.createQuery("select t from Ticket t " + "left join fetch t.createdBy "
							+ "where t.isDeleted = false " + "and t.assignedTo.id = :userId", Ticket.class)
					.setParameter("userId", userId).list();
		}
	}

}
