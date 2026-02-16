package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.TicketDTO;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.mapper.TicketMapper;
import com.erdal.helpdeskpro.service.TicketService;

public class TicketController {
	
	private final TicketService ticketService;
	
	public TicketController(TicketService ticketService) {
		this.ticketService=ticketService;
		
	}
	
	public TicketDTO createTicket(TicketDTO ticketDTO, User user) {
        Ticket ticket = TicketMapper.ticketDTOtoTicket(ticketDTO);
        ticketService.createTicket(ticket, user);
        return TicketMapper.ticketToTicketDTO(ticket);
    }
	
	public TicketDTO getTicket(Long ticketId,User user) {
		Ticket ticket=ticketService.getTicketById(ticketId, user);
		return TicketMapper.ticketToTicketDTO(ticket);
	}
	
	public List<TicketDTO> getAllTicketsForAdmin(User user) {
		List<Ticket> ticketList=ticketService.getAllTickets( user);
		return TicketMapper.ticketListToTicketDTOList(ticketList);	
	}
	
	public List<TicketDTO> getusersTickets(User user) {
		List<Ticket> ticketList=ticketService.getTicketsForUser( user);
		return TicketMapper.ticketListToTicketDTOList(ticketList);
	}
	
	public void uspDateTicetsStatus(Long ticketId,TicketStatus ticketStatus,User user) {
		ticketService.updateStatus(ticketId,ticketStatus, user);
	}
	
	public void assignedUserTickets(Long ticketId,User user) {
				
		ticketService.assignTicket(ticketId, user);
	}
	
	public void deleteTicket(Long ticketId,User user) {
		
		ticketService.deleteTicket(ticketId, user);
		
	}
	
	
	
}
