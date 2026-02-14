package com.erdal.helpdeskpro.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
		if (tickets==null || tickets.isEmpty()) {
			return Collections.emptyList();
		}
		
		return tickets.stream()
				      .map(TicketMapper::ticketToTicketDTO)
				      .collect(Collectors.toList());
		
	}
	public static List<Ticket> ticketDTOListToTicketList(List<TicketDTO> ticketDTOs) {
		
		if (ticketDTOs==null || ticketDTOs.isEmpty()) {
			return Collections.emptyList();
		
	}
		return ticketDTOs.stream()
				         .map(TicketMapper::ticketDTOtoTicket)
				         .collect(Collectors.toList());
	
	
	}
}
