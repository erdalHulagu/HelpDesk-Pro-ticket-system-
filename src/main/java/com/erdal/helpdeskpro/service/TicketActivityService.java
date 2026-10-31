package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.domain.User;

public interface TicketActivityService {

    void createActivity(
            Long ticketId,
            User currentUser,
            String activityType,
            String description,
            String oldValue,
            String newValue
    );

    List<TicketActivity> getActivitiesByTicketId(
            Long ticketId,
            User currentUser
    );
}