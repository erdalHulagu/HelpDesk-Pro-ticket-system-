package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;

public interface TicketRepository  {
	
	 void save(Ticket ticket);
	
	 Ticket findById( Long id);
	
	 List<Ticket> findAll();
	 
	 void update(Ticket ticket);
	 
	 List<Ticket> findAllIncludingDeleted();
	 
	 List<Ticket> findByCreatedBy(User user);
	 

	 void deleteById(Long id);

	Ticket findByIdIncludingDeleted(Long id);
	 
	 List<Ticket> findAllActive();

	List<Ticket> findAllActiveByCreatedBy(Long id);
	

}
