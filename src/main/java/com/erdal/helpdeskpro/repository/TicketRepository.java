package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;

public interface TicketRepository  {
	
	public void save(Ticket ticket);
	
	public Ticket findById( Long id);
	
	public List<Ticket> findAll();
	
	public void delete(Long id);

}
