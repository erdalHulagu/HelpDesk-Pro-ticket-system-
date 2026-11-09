
package com.erdal.helpdeskpro.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.dtos.TicketDTO;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.mapper.TicketMapper;
import com.erdal.helpdeskpro.service.TicketService;

public class TicketController {

	private final TicketService ticketService;

	public TicketController(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	// SLA bilgileriyle birlikte DTO oluşturur.
	private TicketDTO convertToDTO(Ticket ticket) {

		return TicketMapper.ticketToTicketDTO(ticket, ticketService.calculateSlaStatus(ticket),
				ticketService.calculateSlaRemainingTime(ticket));
	}

	// Ticket listesini SLA bilgileriyle birlikte DTO listesine dönüştürür.
	private List<TicketDTO> convertToDTOList(List<Ticket> tickets) {

		if (tickets == null || tickets.isEmpty()) {
			return Collections.emptyList();
		}

		return tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public TicketDTO createTicket(TicketDTO ticketDTO, User currentUser) {

		Ticket ticket = TicketMapper.ticketDTOtoTicket(ticketDTO);

		ticketService.createTicket(ticket, currentUser);

		TicketDTO response = convertToDTO(ticket);

		response.setCreatedById(currentUser.getId());
		response.setCreatedByUsername(currentUser.getUsername());
		response.setCreatedByRole(currentUser.getRole() != null ? currentUser.getRole().name() : null);

		return response;
	}

	public TicketDTO getTicket(Long ticketId, User currentUser) {

		Ticket ticket = ticketService.getTicketById(ticketId, currentUser);

		return convertToDTO(ticket);
	}

	public List<TicketDTO> getAllTicketsForAdmin(User currentUser) {

		List<Ticket> ticketList = ticketService.getAllTicketsForAdmin(currentUser);

		return convertToDTOList(ticketList);
	}

	public List<TicketDTO> getusersTickets(User currentUser) {

		List<Ticket> ticketList = ticketService.getTicketsForUser(currentUser);

		return convertToDTOList(ticketList);
	}

	public void updateTicketStatus(Long ticketId, TicketStatus ticketStatus, User currentUser) {

		ticketService.updateStatus(ticketId, ticketStatus, currentUser);
	}

	public List<TicketDTO> findAllIncludingDeleted(User currentUser) {

		List<Ticket> tickets = ticketService.findAllIncludingDeleted(currentUser);

		return convertToDTOList(tickets);
	}

	public TicketDTO findTicketByIdIncludingDeleted(Long id, User currentUser) {

		Ticket ticket = ticketService.findByIdIncludingDeleted(id, currentUser);

		if (ticket == null) {
			return null;
		}

		return convertToDTO(ticket);
	}

	public void assignTicket(Long ticketId, Long assignedToId, User currentUser) {

		ticketService.assignTicket(ticketId, assignedToId, currentUser);
	}

	public void deleteTicket(Long ticketId, User currentUser) {

		ticketService.deleteTicket(ticketId, currentUser);
	}

	public List<TicketDTO> findAllActive(User currentUser) {

		List<Ticket> tickets = ticketService.findAllActive(currentUser);

		return convertToDTOList(tickets);
	}
}
