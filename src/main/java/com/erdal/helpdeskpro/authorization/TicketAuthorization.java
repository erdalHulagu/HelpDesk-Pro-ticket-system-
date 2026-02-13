package com.erdal.helpdeskpro.authorization;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;

public interface TicketAuthorization {
	
	void canViewTicket(Ticket ticket, User user);

	void canUpdateStatus(Ticket ticket, User user);

	void canAssignTicket(Ticket ticket, User user);

	void canDeleteTicket(Ticket ticket, User user);

	void canComment(Ticket ticket, User user);

}
