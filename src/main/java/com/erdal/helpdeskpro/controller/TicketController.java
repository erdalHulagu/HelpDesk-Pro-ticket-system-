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
	
	public TicketDTO createTicket(TicketDTO ticketDTO, User currentUser) {
        Ticket ticket = TicketMapper.ticketDTOtoTicket(ticketDTO);
        ticketService.createTicket(ticket, currentUser);
        return TicketMapper.ticketToTicketDTO(ticket);
    }
	
	public TicketDTO getTicket(Long ticketId,User currentUser) {
		Ticket ticket=ticketService.getTicketById(ticketId, currentUser);
		return TicketMapper.ticketToTicketDTO(ticket);
	}
	
	public List<TicketDTO> getAllTicketsForAdmin(User currentUser) {
		List<Ticket> ticketList=ticketService.getAllTicketsForAdmin( currentUser);
		return TicketMapper.ticketListToTicketDTOList(ticketList);	
	}
	
	public List<TicketDTO> getusersTickets(User currentUser) {
		List<Ticket> ticketList=ticketService.getTicketsForUser( currentUser);
		return TicketMapper.ticketListToTicketDTOList(ticketList);
	}
	
	public void uspDateTicetsStatus(Long ticketId,TicketStatus ticketStatus,User currentUser) {
		ticketService.updateStatus(ticketId,ticketStatus, currentUser);
	}
	
	public void assignedUserTickets(Long ticketId,User currentUser) {
				
		ticketService.assignTicket(ticketId, currentUser);
	}
	
	public void deleteTicket(Long ticketId,User currentUser) {
		
		ticketService.deleteTicket(ticketId, currentUser);
		
	}
	
	
	
}
