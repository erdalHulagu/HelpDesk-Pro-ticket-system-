package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.TicketActivityType;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.TicketActivityRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.service.TicketActivityService;

public class TicketActivityServiceImpl implements TicketActivityService {

    private final TicketActivityRepository ticketActivityRepository;
    private final TicketRepository ticketRepository;
    private final Authorization authorization;

    public TicketActivityServiceImpl(
            TicketActivityRepository ticketActivityRepository,
            TicketRepository ticketRepository,
            Authorization authorization) {

        this.ticketActivityRepository = ticketActivityRepository;
        this.ticketRepository = ticketRepository;
        this.authorization = authorization;
    }

    @Override
    public void createActivity(
            Long ticketId,
            User currentUser,
            TicketActivityType activityType,
            String description,
            String oldValue,
            String newValue) {

        if (ticketId == null || currentUser == null
                || currentUser.getId() == null
                || activityType == null || description == null) {

            throw new IllegalArgumentException(
                    "Activity information is incomplete.");
        }

        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new ResourceNotFoundExeption(
                    ExceptionMessage.TICKET_NOT_FOUND);
        }

        TicketActivity activity = new TicketActivity();

        activity.setTicket(ticket);
        activity.setPerformedBy(currentUser);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setOldValue(oldValue);
        activity.setNewValue(newValue);

        ticketActivityRepository.save(activity);
    }

    @Override
    public List<TicketActivity> getActivitiesByTicketId(
            Long ticketId,
            User currentUser) {

        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new ResourceNotFoundExeption(
                    ExceptionMessage.TICKET_NOT_FOUND);
        }

        authorization.canViewTicketActivity(ticket, currentUser);

        return ticketActivityRepository.findAllByTicketId(ticketId);
    }
}