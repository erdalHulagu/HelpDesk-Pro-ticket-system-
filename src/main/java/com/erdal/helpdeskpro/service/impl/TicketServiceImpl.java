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

	    //  ticket var mı
	    if (ticket == null) {
	        throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
	    }

	    //  soft delete
	    if (ticket.isDeleted()) {
	        throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
	    }

	    // role kontrol
	    if (user.getRole() == Role.EMPLOYEE) {
	        throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
	    }

	    //  CLOSED ise dokunulmaz
	    if (ticket.getStatus() == TicketStatus.CLOSED) {
	        throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
	    }

	    //  lifecycle validation
	    TicketStatus current = ticket.getStatus();

	    boolean valid =
	            (current == TicketStatus.OPEN && newStatus == TicketStatus.IN_PROGRESS) ||
	            (current == TicketStatus.IN_PROGRESS && newStatus == TicketStatus.RESOLVED) ||
	            (current == TicketStatus.RESOLVED && newStatus == TicketStatus.CLOSED);

	    if (!valid) {
	        throw new BadRequestExeption(ExceptionMessage.INVALID_STATUS_TRANSITION);
	    }

	    //  update
	    ticket.setStatus(newStatus);
	    ticketRepository.save(ticket);
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
		
		return ticket;
	}

	@Override
	public List<Ticket> getTicketsForUser(User user) {
		List<Ticket> tickets = ticketRepository.findAll();

		return tickets.stream()
			    .filter(t -> t.getCreatedBy().getId().equals(user.getId()))
			    .filter(t -> !t.isDeleted())
			    .collect(Collectors.toList());

		

	}

	@Override
	public void assignTicket(Long ticketId, User user) {

		Ticket ticket = ticketRepository.findById(ticketId);

		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}
		if (user.getRole() != Role.ADMIN || user.getRole() != Role.IT_SUPPORT) {
		    throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		if (ticket.getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		ticket.setAssignedTo(user);

	}


	@Override
	public void deleteTicket(Long ticketId, User user) {

	    Ticket ticket = ticketRepository.findById(ticketId);

	    //  ticket var mı
	    if (ticket == null) {
	        throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
	    }

	    // lifecycle kontrolü
	    if (ticket.getStatus() == TicketStatus.CLOSED) {
	        throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
	    }

	    // yetki kontrolü
	    // EMPLOYEE sadece kendi ticket’ını silebilir
	    if (user.getRole() == Role.EMPLOYEE &&
	        !ticket.getCreatedBy().getId().equals(user.getId())) {
	        throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
	    }

	    // soft delete
	    ticket.setDeleted(true);

	    // persist
	    ticketRepository.save(ticket);

	
	}


	
}
