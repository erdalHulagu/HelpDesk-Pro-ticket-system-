
package com.erdal.helpdeskpro.authorization;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;

public interface Authorization {

    void canViewTicket(Ticket ticket, User user);

    void canUpdateStatus(Ticket ticket, User user);

    void canAssignTicket(Ticket ticket, User user);

    void canDeleteTicket(Ticket ticket, User user);

    void canComment(Ticket ticket, User user);

    void canViewAllActiveTickets(User user);

    void canViewComments(Ticket ticket, User user);

    void canCheckUserProfile(Attachment attachment, User user);

	void canManageUser(User currentUser, Long targetUserId);
}
