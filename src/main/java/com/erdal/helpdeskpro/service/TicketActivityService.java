package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.TicketActivityType;

public interface TicketActivityService {

    void createActivity(
            Long ticketId,
            User currentUser,
            TicketActivityType ticketCreated,
            String description,
            String oldValue,
            String newValue
    );

    List<TicketActivity> getActivitiesByTicketId(
            Long ticketId,
            User currentUser
    );

	List<TicketActivity> getAllActivities(User currentUser);
}