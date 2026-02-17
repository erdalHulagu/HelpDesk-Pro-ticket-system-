package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.TicketStatus;

public interface TicketService {
	
	void createTicket(Ticket ticket, User currentUser);
	
	Ticket getTicketById(Long ticketId, User currentUser);
	
	List<Ticket> getTicketsForUser(User currentUser);
	
	void assignTicket(Long ticketId,User currentUser);
	
	void updateStatus(Long ticketId,TicketStatus ticketStatus,User currentUser);
	
	void  deleteTicket(Long ticketId, User currentUser);

	List<Ticket> getAllTicketsForAdmin( User currentUser);

}
