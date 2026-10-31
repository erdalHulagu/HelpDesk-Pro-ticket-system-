
package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.TicketActivity;

public interface TicketActivityRepository {

    void save(TicketActivity activity);

    List<TicketActivity> findAllByTicketId(Long ticketId);
}
