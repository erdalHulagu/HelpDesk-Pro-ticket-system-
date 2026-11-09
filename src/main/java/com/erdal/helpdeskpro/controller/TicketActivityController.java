
package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.domain.dtos.TicketActivityDTO;
import com.erdal.helpdeskpro.mapper.TicketActivityMapper;
import com.erdal.helpdeskpro.service.TicketActivityService;

public class TicketActivityController {

	private final TicketActivityService ticketActivityService;
	private final TicketActivityMapper ticketActivityMapper;

	public TicketActivityController(TicketActivityService ticketActivityService) {

		this.ticketActivityService = ticketActivityService;
		this.ticketActivityMapper = new TicketActivityMapper();
	}

	public List<TicketActivityDTO> getActivitiesByTicketId(Long ticketId, User currentUser) {

		return ticketActivityMapper.toDTOList(ticketActivityService.getActivitiesByTicketId(ticketId, currentUser));
	}

	public List<TicketActivityDTO> getAllActivities(User currentUser) {

		return ticketActivityMapper.toDTOList(ticketActivityService.getAllActivities(currentUser));
	}

}
