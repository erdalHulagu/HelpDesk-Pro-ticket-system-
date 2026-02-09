package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;

public interface TicketRepository  {
	
	 void save(Ticket ticket);
	
	 Ticket findById( Long id);
	
	 List<Ticket> findAll();
	 
	 List<Ticket> findByCreatedBy(User user);
	 
	 List<Ticket> findAllActive();
	
	 void deleteById(Long id);

}
