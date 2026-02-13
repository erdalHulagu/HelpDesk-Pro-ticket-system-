package com.erdal.helpdeskpro.mapper;

import java.util.List;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.TicketDTO;

public class TicketMapper {

	public static Ticket ticketDTOtoTicket(TicketDTO ticketDTO) {

		User assignedUser = new User();
		assignedUser.setId(ticketDTO.getAssignedToId());

		User createdByUser = new User();
		createdByUser.setId(ticketDTO.getCreatedById());

		Ticket ticket = new Ticket();

		ticket.setId(ticketDTO.getId());
		ticket.setStatus(ticketDTO.getStatus());
		ticket.setDescription(ticketDTO.getDescription());
		ticket.setDeleted(ticketDTO.isDeleted());
		ticket.setCreatedBy(createdByUser);
		ticket.setAssignedTo(assignedUser);
		ticket.setTitle(ticketDTO.getTitle());
		
		return ticket;

	}
	
	public static TicketDTO ticketToTicketDTO(Ticket ticket) {
		
		
		TicketDTO ticketDTO = new TicketDTO();
		
		ticketDTO.setId(ticket.getId());
		ticketDTO.setStatus(ticket.getStatus());
		ticketDTO.setDescription(ticket.getDescription());
		ticketDTO.setDeleted(ticket.isDeleted());
		ticketDTO.setCreatedById(ticket.getCreatedBy().getId());
		ticketDTO.setAssignedToId(ticket.getAssignedTo().getId());
		ticketDTO.setTitle(ticket.getTitle());
		
		return ticketDTO;
		
	}
	public static List<TicketDTO> ticketListToTicketDTOList(List<Ticket> tickets) {
		
		
		TicketDTO ticketDTO = new TicketDTO();
		
		ticketDTO.setId(ticket.getId());
		ticketDTO.setStatus(ticket.getStatus());
		ticketDTO.setDescription(ticket.getDescription());
		ticketDTO.setDeleted(ticket.isDeleted());
		ticketDTO.setCreatedById(ticket.getCreatedBy().getId());
		ticketDTO.setAssignedToId(ticket.getAssignedTo().getId());
		ticketDTO.setTitle(ticket.getTitle());
		
		return ticketDTO;
		
	}
	public static List<Ticket> ticketDTOListToTicketList(List<TicketDTO> ticketDTOs) {
		
		
		TicketDTO ticketDTO = new TicketDTO();
		
		ticketDTO.setId(ticket.getId());
		ticketDTO.setStatus(ticket.getStatus());
		ticketDTO.setDescription(ticket.getDescription());
		ticketDTO.setDeleted(ticket.isDeleted());
		ticketDTO.setCreatedById(ticket.getCreatedBy().getId());
		ticketDTO.setAssignedToId(ticket.getAssignedTo().getId());
		ticketDTO.setTitle(ticket.getTitle());
		
		return ticketDTO;
		
	}
	
	

}
