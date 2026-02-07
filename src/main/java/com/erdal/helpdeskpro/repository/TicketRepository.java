package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;

public interface TicketRepository  {
	
	 void save(Ticket ticket);
	
	 Ticket findById( Long id);
	
	 List<Ticket> findAll();
	
	 void deleteById(Long id);

}
