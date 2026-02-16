package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.TicketStatus;

public interface TicketService {
	
	void createTicket(Ticket ticket, User user);
	
	Ticket getTicketById(Long ticketId, User user);
	
	List<Ticket> getTicketsForUser(User user);
	
	void assignTicket(Long ticketId,User user);
	
	void updateStatus(Long ticketId,TicketStatus ticketStatus,User user);
	
	void  deleteTicket(Long ticketId, User user);

	List<Ticket> getAllTickets(Long ticketId, User user);

}
