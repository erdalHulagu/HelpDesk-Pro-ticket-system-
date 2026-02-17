package com.erdal.helpdeskpro.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.authorization.Authorization;
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

	private TicketRepository ticketRepository;
	private Authorization authorization;

	public TicketServiceImpl(TicketRepository ticketRepository, Authorization authorization) {
		this.ticketRepository = ticketRepository;
		this.authorization = authorization;
	}

	@Override
	public void createTicket(Ticket ticket, User currentUser) {
		ticket.setCreatedBy(currentUser);
		ticket.setStatus(TicketStatus.OPEN);
		ticketRepository.save(ticket);
	}

	@Override
	public void updateStatus(Long ticketId, TicketStatus newStatus, User currentUser) {

		Ticket ticket = ticketRepository.findById(ticketId);
		authorization.canUpdateStatus(ticket, currentUser);

		// lifecycle validation

		if (!isValidTransition(ticket.getStatus(), newStatus)) {
			throw new BadRequestExeption(ExceptionMessage.INVALID_STATUS_TRANSITION);
		}

		// update
		ticket.setStatus(newStatus);
		ticketRepository.save(ticket);
	}

	private boolean isValidTransition(TicketStatus current, TicketStatus next) {
		return (current == TicketStatus.OPEN && next == TicketStatus.IN_PROGRESS)
				|| (current == TicketStatus.IN_PROGRESS && next == TicketStatus.RESOLVED)
				|| (current == TicketStatus.RESOLVED && next == TicketStatus.CLOSED);
	}

	@Override
	public Ticket getTicketById(Long ticketId, User currentUser) {

		Ticket ticket = ticketRepository.findById(ticketId);
		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		if (!ticket.getCreatedBy().getId().equals(currentUser.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		return ticket;
	}

	@Override
	public List<Ticket> getTicketsForUser(User currentUser) {
		List<Ticket> tickets = ticketRepository.findAll();

		return tickets.stream().filter(t -> t.getCreatedBy().getId().equals(currentUser.getId())).filter(t -> !t.isDeleted())
				.collect(Collectors.toList());

	}

	@Override
	public void assignTicket(Long ticketId, User currentUser) {

		Ticket ticket = ticketRepository.findById(ticketId);
		authorization.canAssignTicket(ticket, currentUser);

		ticket.setAssignedTo(currentUser);

	}

	@Override
	public void deleteTicket(Long ticketId, User currentUser) {

		Ticket ticket = ticketRepository.findById(ticketId);

		authorization.canDeleteTicket(ticket, currentUser);

		ticket.setDeleted(true);

		// persist
		ticketRepository.save(ticket);

	}

	@Override
	public List<Ticket> getAllTicketsForAdmin(User currentUser) {
		if (currentUser.getRole()!=Role.ADMIN) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		
		return ticketRepository.findAll();
	}

}
