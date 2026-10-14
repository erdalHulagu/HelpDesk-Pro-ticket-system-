
package com.erdal.helpdeskpro.authorization;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;

public class AuthorizationImpl implements Authorization {

	// =========================
	// PUBLIC AUTHORIZATION METHODS
	// =========================

	@Override
	public void canViewTicket(Ticket ticket, User user) {
		checkTicketAndUser(ticket, user);
		checkNotDeleted(ticket);
		checkTicketOwner(ticket, user);
	}

	@Override
	public void canUpdateStatus(Ticket ticket, User user) {
		checkTicketAndUser(ticket, user);
		checkNotDeleted(ticket);
		checkNotClosed(ticket);
		checkAdminOrSupport(user);
	}

	@Override
	public void canAssignTicket(Ticket ticket, User user) {
		checkTicketAndUser(ticket, user);
		checkAdminOrSupport(user);
		checkNotDeleted(ticket);
		checkNotClosed(ticket);
	}

	@Override
	public void canDeleteTicket(Ticket ticket, User user) {
	    checkTicketAndUser(ticket, user);
	    checkNotDeleted(ticket);
	    checkNotClosed(ticket);
	    checkAdmin(user);
	}

	@Override
	public void canComment(Ticket ticket, User user) {
		checkTicketAndUser(ticket, user);
		checkNotDeleted(ticket);
		checkNotClosed(ticket);
		checkTicketOwner(ticket, user);
	}

	@Override
	public void canViewAllActiveTickets(User user) {
		checkUser(user);
	}

	// =========================
	// HELPER METHODS
	// =========================

	private void checkTicketAndUser(Ticket ticket, User user) {
		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}

		checkUser(user);
	}

	private void checkUser(User user) {
		if (user == null || user.getRole() == null||user.getRole()!=Role.ADMIN) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
	}

	private void checkNotDeleted(Ticket ticket) {
		if (ticket.isDeleted()) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
		}
	}

	private void checkNotClosed(Ticket ticket) {
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}
	}

	private void checkTicketOwner(Ticket ticket, User user) {
		if (user.getRole() != Role.EMPLOYEE) {
			return;
		}

		if (user.getId() == null || ticket.getCreatedBy() == null
				|| !user.getId().equals(ticket.getCreatedBy().getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
	}

	private void checkAdmin(User user) {
	    if (user.getRole() != Role.ADMIN) {
	        throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
	    }
	}
	private void checkAdminOrSupport(User user) {
		if (user.getRole() != Role.ADMIN && user.getRole() != Role.IT_SUPPORT) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
	}
}
