package com.erdal.helpdeskpro.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.service.TicketService;

public class TicketServiceImpl implements TicketService {

	TicketRepository ticketRepository;

	public TicketServiceImpl(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	@Override
	public void createTicket(Ticket ticket, User user) {
		ticket.setCreatedBy(user);
		ticket.setStatus(TicketStatus.OPEN);
		ticketRepository.save(ticket);
	}

	@Override
	public void updateStatus(Long ticketId, TicketStatus newStatus, User user) {
		Ticket ticket = ticketRepository.findById(ticketId);

		// Role kontrol
		if (user.getRole() == Role.EMPLOYEE.name() && !ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);

		}

		// Lifecycle kuralı
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		ticket.setStatus(newStatus); // Hibernate dirty checking
	}

	@Override
	public Ticket getTicketById(Long ticketId, User user) {

		Ticket ticket = ticketRepository.findById(ticketId);
		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		if (!ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		return ticket;
	}

	@Override
	public List<Ticket> getTicketsForUser(User user) {
		List<Ticket> tickets = ticketRepository.findAll();

		List<Ticket> usersTickets = tickets.stream().filter(t -> t.getCreatedBy().getId().equals(user.getId()))
				.collect(Collectors.toList());

		if (usersTickets.isEmpty()) {
			throw new BadRequestExeption(ExceptionMessage.NO_TICKETS_FOUND_FOR_USER);
		}

		return usersTickets;

	}

	@Override
	public void assignTicket(Long ticketId, User user) {

		Ticket ticket = ticketRepository.findById(ticketId);

		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		if (!ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		ticket.setAssignedTo(user);

	}

	@Override
	public Ticket deleteTicket(Long ticketId, User user) {
		Ticket ticket = ticketRepository.deleteById(ticketId);

		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		if (!ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		return ticket;
	}

}
