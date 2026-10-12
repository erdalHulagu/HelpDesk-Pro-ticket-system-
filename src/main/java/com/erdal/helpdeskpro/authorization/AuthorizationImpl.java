package com.erdal.helpdeskpro.authorization;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;

public class AuthorizationImpl implements Authorization {

	@Override
	public void canViewTicket(Ticket ticket, User user) {
		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}

		// soft delete
		if (ticket.isDeleted()) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
		}
		// CLOSED ise dokunulmaz
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		if (user.getRole() == Role.EMPLOYEE && (!ticket.getCreatedBy().getId().equals(user.getId()))) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);

		}

	}

	@Override
	public void canUpdateStatus(Ticket ticket, User user) {

		if (ticket == null) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}

		checkUser(user);
		
		if (ticket.isDeleted()) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
		}

		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}
		
		
		if (user.getRole() == Role.EMPLOYEE && !ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
	}

	@Override
	public void canAssignTicket(Ticket ticket, User user) {

		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		
		checkUser(user);
		
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}
		

		if (user.getRole() != Role.ADMIN && user.getRole() != Role.IT_SUPPORT) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

	}

	@Override
	public void canDeleteTicket(Ticket ticket, User user) {

		// ticket var mı
		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		
		checkUser(user);
		// soft delete
		if (ticket.isDeleted()) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
		}
		// lifecycle kontrolü
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		// yetki kontrolü
		// EMPLOYEE sadece kendi ticket’ını silebilir
		if (user.getRole() == Role.EMPLOYEE && !ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

	}

	@Override
	public void canComment(Ticket ticket, User user) {
		// ticket var mı
		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		
		checkUser(user);
		// soft delete
		if (ticket.isDeleted()) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
		}
		// lifecycle kontrolü
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		// yetki kontrolü
		// EMPLOYEE sadece kendi ticket’ını silebilir
		if (user.getRole() == Role.EMPLOYEE && !ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

	}

	@Override
	public void canViewAllActiveTickets(User user) {

		if (user == null) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		
		checkUser(user);
		
		if (user.getRole() != Role.ADMIN && user.getRole() != Role.IT_SUPPORT && user.getRole() != Role.EMPLOYEE) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		
	}

	private void checkUser(User user) {
		if (user == null || user.getRole() == null) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
	}

}
