package com.erdal.helpdeskpro.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.dtos.TicketDTO;
import com.erdal.helpdeskpro.domain.dtos.UserDTO;

public class TicketMapper {

    public static Ticket ticketDTOtoTicket(TicketDTO ticketDTO) {

        Ticket ticket = new Ticket();

        ticket.setId(ticketDTO.getId());
        ticket.setStatus(ticketDTO.getStatus());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setCategory(ticketDTO.getCategory());
        ticket.setPriority(ticketDTO.getPriority());
        ticket.setDeleted(ticketDTO.isDeleted());
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setResolvedAt(ticketDTO.getResolvedAt());
        ticket.setSlaDueAt(ticketDTO.getSlaDueAt());

        return ticket;
    }

    public static TicketDTO ticketToTicketDTO(Ticket ticket) {

        TicketDTO ticketDTO = new TicketDTO();

        ticketDTO.setId(ticket.getId());
        ticketDTO.setStatus(ticket.getStatus());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setCategory(ticket.getCategory());
        ticketDTO.setPriority(ticket.getPriority());
        ticketDTO.setDeleted(ticket.isDeleted());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setResolvedAt(ticket.getResolvedAt());
        ticketDTO.setSlaDueAt(ticket.getSlaDueAt());

        ticketDTO.setCreatedById(
            ticket.getCreatedBy() != null
                ? ticket.getCreatedBy().getId()
                : null
        );

        ticketDTO.setCreatedByUsername(
            ticket.getCreatedBy() != null
                ? ticket.getCreatedBy().getUsername()
                : null
        );

        ticketDTO.setCreatedByRole(
            ticket.getCreatedBy() != null
                    && ticket.getCreatedBy().getRole() != null
                ? ticket.getCreatedBy().getRole().name()
                : null
        );

        ticketDTO.setAssignedToId(
            ticket.getAssignedTo() != null
                ? ticket.getAssignedTo().getId()
                : null
        );
        ticketDTO.setAssignedToUsername(
        	    ticket.getAssignedTo() != null
        	        ? ticket.getAssignedTo().getUsername()
        	        : null
        	);
        
        ticketDTO.setAssetId(
        	    ticket.getAsset() != null
        	        ? ticket.getAsset().getId()
        	        : null
        	);

        	ticketDTO.setAssetTag(
        	    ticket.getAsset() != null
        	        ? ticket.getAsset().getAssetTag()
        	        : null
        	);

        	ticketDTO.setAssetName(
        	    ticket.getAsset() != null
        	        ? ticket.getAsset().getName()
        	        : null
        	);

        return ticketDTO;
    }

    // SLA bilgileriyle birlikte DTO oluşturur.
    public static TicketDTO ticketToTicketDTO(
            Ticket ticket,
            String slaStatus,
            String slaRemainingTime) {

        TicketDTO ticketDTO = ticketToTicketDTO(ticket);

        ticketDTO.setSlaStatus(slaStatus);
        ticketDTO.setSlaRemainingTime(slaRemainingTime);

        return ticketDTO;
    }

    public static List<TicketDTO> ticketListToTicketDTOList(
            List<Ticket> tickets) {

        if (tickets == null || tickets.isEmpty()) {
            return Collections.emptyList();
        }

        return tickets.stream()
                .map(TicketMapper::ticketToTicketDTO)
                .collect(Collectors.toList());
    }

    public static List<Ticket> ticketDTOListToTicketList(
            List<TicketDTO> ticketDTOs) {

        if (ticketDTOs == null || ticketDTOs.isEmpty()) {
            return Collections.emptyList();
        }

        return ticketDTOs.stream()
                .map(TicketMapper::ticketDTOtoTicket)
                .collect(Collectors.toList());
    }

    public static Ticket userDTOtoTicket(UserDTO userDTO) {

        Ticket ticket = new Ticket();

        if (userDTO.getId() != null) {
            User user = new User();
            user.setId(userDTO.getId());
            ticket.setCreatedBy(user);
        }

        ticket.setStatus(null);
        ticket.setId(null);

        return ticket;
    }
}
