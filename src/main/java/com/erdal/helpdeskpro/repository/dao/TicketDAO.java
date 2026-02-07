package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.repository.TicketRepository;

public class TicketDAO implements TicketRepository {
	
	private SessionFactory sessionFactory;
	
	public TicketDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
		
	}
	
	@Override
	public void save(Ticket ticket) {
		Session session =sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		session.persist(ticket);
		
		transaction.commit();
		
		session.close();
		
	}
	
	@Override
	public Ticket findById( Long id) {
		Session session =sessionFactory.openSession();
		
		Ticket ticket=session.get(Ticket.class, id);
		
		session.close();
		return ticket;
		
	}
	
	@Override
	public List<Ticket> findAll() {
		
		Session session =sessionFactory.openSession();
		
		List<Ticket> tickets=session.createQuery("from Ticket",Ticket.class).list();
		
		session.close();
		
		return tickets;
		
		
	}
	
	@Override
	public void deleteById(Long id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Ticket ticket = session.get(Ticket.class, id);
		if (ticket != null) {
		    ticket.setDeleted(true);
		}
		if (ticket != null && !ticket.isDeleted()) {
		    ticket.setDeleted(true);
		}
		tx.commit();
		session.close();
	}

}
