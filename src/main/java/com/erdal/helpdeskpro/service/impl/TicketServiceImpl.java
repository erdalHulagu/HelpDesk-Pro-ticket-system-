package com.erdal.helpdeskpro.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketPriority;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.IllegalArgumentException;
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

	public TicketServiceImpl(TicketRepository ticketrepository) {
		this.ticketRepository = ticketRepository;
	}
	@Override
	public void createTicket(Ticket ticket, User currentUser) {
	    ticket.setCreatedBy(currentUser);
	    ticket.setStatus(TicketStatus.OPEN);
	    ticket.setDeleted(false);

	    if (ticket.getPriority() == null) {
	        ticket.setPriority(TicketPriority.MEDIUM);
	    }

	    ticket.setCreatedAt(LocalDateTime.now());
	    ticket.setSlaDueAt(
	        ticket.getCreatedAt().plusHours(getSlaHours(ticket.getPriority()))
	    );
	    ticket.setResolvedAt(null);

	    ticketRepository.save(ticket);
	}

	private int getSlaHours(TicketPriority priority) {
	    return switch (priority) {
	        case LOW -> 72;
	        case MEDIUM -> 24;
	        case HIGH -> 8;
	        case CRITICAL -> 4;
	    };
	}

	@Override
	public void updateStatus(Long ticketId, TicketStatus newStatus, User currentUser) {

	    Ticket ticket = ticketRepository.findById(ticketId);

	    if (ticket == null) {
	        throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
	    }

	    if (newStatus == null) {
	        throw new BadRequestExeption(ExceptionMessage.INVALID_STATUS_TRANSITION);
	    }

	    authorization.canUpdateStatus(ticket, currentUser);

	    // Status yaşam döngüsü kontrolü
	    if (!isValidTransition(ticket.getStatus(), newStatus)) {
	        throw new BadRequestExeption(
	            ExceptionMessage.INVALID_STATUS_TRANSITION
	        );
	    }

	    // Güncelleme
	    ticket.setStatus(newStatus);

	    if (newStatus == TicketStatus.RESOLVED) {
	        ticket.setResolvedAt(LocalDateTime.now());
	    }
	    ticketRepository.update(ticket);
	}

	private boolean isValidTransition(TicketStatus current, TicketStatus next) {
	    if (current == null || next == null) {
	        return false;
	    }

	    return switch (current) {
	        case OPEN -> next == TicketStatus.IN_PROGRESS;
	        case IN_PROGRESS -> next == TicketStatus.RESOLVED;
	        case RESOLVED -> next == TicketStatus.CLOSED;
	        case CLOSED -> false;
	    };
	}

	@Override
	public Ticket getTicketById(Long ticketId, User currentUser) {

	    if (currentUser == null) {
	        throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
	    }

	    Ticket ticket = ticketRepository.findById(ticketId);

	    if (ticket == null) {
	        throw new ResourceNotFoundExeption(
	                ExceptionMessage.TICKET_NOT_FOUND
	        );
	    }

	    authorization.canViewTicket(ticket, currentUser);

	    return ticket;
	}
	
	@Override
	public List<Ticket> getTicketsForUser(User currentUser) {
		
		List<Ticket> tickets = ticketRepository.findAllActiveByCreatedBy(currentUser.getId());

		return tickets.stream().filter(t -> t.getCreatedBy().getId().equals(currentUser.getId()))
				.filter(t -> !t.isDeleted()).collect(Collectors.toList());

	}

	@Override
	public void assignTicket(Long ticketId, User currentUser) {

		Ticket ticket = ticketRepository.findById(ticketId);
		authorization.canAssignTicket(ticket, currentUser);

		ticket.setAssignedTo(currentUser);

	}


	@Override
	public List<Ticket> getAllTicketsForAdmin(User currentUser) {
		if (currentUser.getRole() != Role.ADMIN) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		return ticketRepository.findAll();
	}

	@Override
	public List<Ticket> getAllTickets(User currentUser) {

	    if (currentUser == null) {
	        throw new IllegalArgumentException("User cannot be null");
	    }

	    if (currentUser.getRole() == Role.ADMIN) {
	        return ticketRepository.findAll();
	    }

	    return ticketRepository.findAllActiveByCreatedBy(currentUser.getId());
	}
	@Override
	public List<Ticket> findAllIncludingDeleted(User currentUser) {

		if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		return ticketRepository.findAllIncludingDeleted();
	}

	@Override
	public Ticket findByIdIncludingDeleted(Long id, User currentUser) {

		if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		return ticketRepository.findByIdIncludingDeleted(id);
	}



@Override
public List<Ticket> findAllActive(User currentUser) {
    if (currentUser == null || currentUser.getRole() == null
            || currentUser.getId() == null) {
        throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
    }

    Role role = currentUser.getRole();

    if (role == Role.EMPLOYEE) {
        return ticketRepository.findAllActiveByCreatedBy(currentUser.getId());
    }

    if (role == Role.IT_SUPPORT) {
        List<Ticket> assignedTickets =
                ticketRepository.findAllActiveByAssignedTo(currentUser.getId());

        List<Ticket> createdTickets =
                ticketRepository.findAllActiveByCreatedBy(currentUser.getId());

        Map<Long, Ticket> combined = new LinkedHashMap<>();

        for (Ticket ticket : assignedTickets) {
            if (!ticket.isDeleted()) {
                combined.put(ticket.getId(), ticket);
            }
        }

        for (Ticket ticket : createdTickets) {
            if (!ticket.isDeleted()) {
                combined.put(ticket.getId(), ticket);
            }
        }

        return new ArrayList<>(combined.values());
    }

    if (role == Role.ADMIN || role == Role.IT_MANAGER) {
        return ticketRepository.findAllActive();
    }

    throw  new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
}


	
	@Override
	public void deleteTicket(Long ticketId, User currentUser) {

	    Ticket ticket = ticketRepository.findById(ticketId);

	    if (ticket == null) {
	        throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
	    }

	    authorization.canDeleteTicket(ticket, currentUser);

	    ticket.setDeleted(true);
	    ticketRepository.update(ticket);
	}
}
