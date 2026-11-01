
package com.erdal.helpdeskpro.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketActivityType;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
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

        List<TicketActivity> activities =
                ticketActivityRepository.findAllByTicketId(ticketId);

        return filterInternalActivities(activities, currentUser);
    }

    @Override
    public List<TicketActivity> getAllActivities(User currentUser) {

        if (currentUser == null
                || currentUser.getId() == null
                || currentUser.getRole() == null) {

            throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        }

        List<TicketActivity> allActivities =
                ticketActivityRepository.findAll();

        List<TicketActivity> accessibleActivities = new ArrayList<>();

        for (TicketActivity activity : allActivities) {

            try {
                Ticket ticket = activity.getTicket();

                if (ticket == null) {
                    continue;
                }

                authorization.canViewTicketActivity(ticket, currentUser);

                if (isInternalNote(activity)
                        && !canViewInternalNote(ticket, currentUser)) {
                    continue;
                }

                accessibleActivities.add(activity);

            } catch (BadRequestExeption e) {
                // Skip activities belonging to inaccessible tickets.
            }
        }

        return accessibleActivities;
    }

    private List<TicketActivity> filterInternalActivities(
            List<TicketActivity> activities,
            User currentUser) {

        List<TicketActivity> filteredActivities = new ArrayList<>();

        for (TicketActivity activity : activities) {

            if (isInternalNote(activity)
                    && !canViewInternalNote(
                            activity.getTicket(), currentUser)) {
                continue;
            }

            filteredActivities.add(activity);
        }

        return filteredActivities;
    }

    private boolean isInternalNote(TicketActivity activity) {

        return activity.getActivityType()
                == TicketActivityType.INTERNAL_NOTE_ADDED;
    }

    private boolean canViewInternalNote(
            Ticket ticket,
            User currentUser) {

        if (ticket == null
                || currentUser == null
                || currentUser.getRole() == null
                || currentUser.getId() == null) {

            return false;
        }

        Role role = currentUser.getRole();

        if (role == Role.ADMIN || role == Role.IT_MANAGER) {
            return true;
        }

        return role == Role.IT_SUPPORT
                && ticket.getAssignedTo() != null
                && ticket.getAssignedTo().getId() != null
                && ticket.getAssignedTo().getId()
                        .equals(currentUser.getId());
    }
}