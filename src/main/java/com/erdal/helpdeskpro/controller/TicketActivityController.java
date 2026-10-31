
package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.service.TicketActivityService;

public class TicketActivityController {

    private final TicketActivityService ticketActivityService;

    public TicketActivityController(TicketActivityService ticketActivityService) {
        this.ticketActivityService = ticketActivityService;
    }

    public List<TicketActivity> getActivitiesByTicketId(
            Long ticketId,
            User currentUser) {

        return ticketActivityService.getActivitiesByTicketId(
                ticketId,
                currentUser
        );
    }
}
