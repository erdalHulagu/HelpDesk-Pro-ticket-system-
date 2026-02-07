package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.service.TicketService;

public class TicketServiceImpl implements TicketService{
	
	TicketRepository ticketRepository;
	
	public TicketServiceImpl(TicketRepository ticketRepository) {
		this.ticketRepository=ticketRepository;
	}
	
	
	public void createTicket(Ticket ticket, User user) {
        ticket.setCreatedBy(user);
        ticket.setStatus(TicketStatus.OPEN);
        ticketRepository.save(ticket);
    }

    public void updateStatus(Long ticketId, TicketStatus newStatus, User user) {
        Ticket ticket = ticketRepository.findById(ticketId);
        
        // Role kontrol
        if(user.getRole() == Role.EMPLOYEE.name() && !ticket.getCreatedBy().getId().equals(user.getId())) {
        	throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        	
        }
        
        // Lifecycle kuralı
        if(ticket.getStatus() == TicketStatus.CLOSED) {
        	throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
        }

        ticket.setStatus(newStatus); // Hibernate dirty checking
    }




	@Override
	public Ticket getTicketById(Long id,User user) {
		
		Ticket  ticket=ticketRepository.findById(id);
		if (!ticket.getCreatedBy().getId().equals(user.getId())) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}
		 if(ticket.getStatus() == TicketStatus.CLOSED) {
	        	throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
	    }
		
		return ticket;
	}


	@Override
	public List<Ticket> getTicketsForUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void assignTicket(User user) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteTicket(Long id) {
		// TODO Auto-generated method stub
		
	}

}
