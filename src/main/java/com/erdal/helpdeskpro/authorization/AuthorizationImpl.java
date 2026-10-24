
package com.erdal.helpdeskpro.authorization;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;

public class AuthorizationImpl implements Authorization {

    // =========================
    // PUBLIC AUTHORIZATION METHODS
    // =========================

    @Override
    public void canViewTicket(Ticket ticket, User user) {
        checkTicketAndUser(ticket, user);
        checkNotDeleted(ticket);
        checkTicketOwner(ticket, user);
    }

    @Override
    public void canCheckUserProfile(Attachment attachment, User user) {
        checkUser(user);
        checkProfileOwner(attachment, user);
    }

    @Override
    public void canUpdateStatus(Ticket ticket, User user) {
        checkTicketAndUser(ticket, user);
        checkNotDeleted(ticket);
        checkNotClosed(ticket);
        checkAdminManagerOrSupport(user);
        checkTicketAccess(ticket, user);
    }

    @Override
    public void canAssignTicket(Ticket ticket, User user) {
        checkTicketAndUser(ticket, user);
        checkNotDeleted(ticket);
        checkNotClosed(ticket);
        checkAdminOrManager(user);
    }

    @Override
    public void canDeleteTicket(Ticket ticket, User user) {
        checkTicketAndUser(ticket, user);
        checkNotDeleted(ticket);
        checkAdmin(user);
        checkClosed(ticket);
    }

    @Override
    public void canComment(Ticket ticket, User user) {
        checkTicketAndUser(ticket, user);
        checkNotDeleted(ticket);
        checkNotClosed(ticket);
        checkTicketAccess(ticket, user);
    }

    @Override
    public void canViewAllActiveTickets(User user) {
        checkUser(user);

        if (user.getRole() != Role.ADMIN
                && user.getRole() != Role.IT_MANAGER
                && user.getRole() != Role.IT_SUPPORT
                && user.getRole() != Role.EMPLOYEE) {
            throw notAllowed();
        }
    }

    @Override
    public void canViewComments(Ticket ticket, User user) {
        checkTicketAndUser(ticket, user);
        checkNotDeleted(ticket);
        checkTicketAccess(ticket, user);
    }

    // =========================
    // HELPER METHODS
    // =========================
    

    @Override
    public void canManageUser(User currentUser, Long targetUserId) {
        checkUser(currentUser);

        if (currentUser.getRole() != Role.ADMIN) {
            throw notAllowed();
        }

        if (currentUser.getId() == null || targetUserId == null) {
            throw notAllowed();
        }
    }


    private void checkTicketAndUser(Ticket ticket, User user) {
        if (ticket == null) {
            throw new ResourceNotFoundExeption(
                    ExceptionMessage.TICKET_NOT_FOUND
            );
        }

        checkUser(user);
    }

    private void checkUser(User user) {
        if (user == null || user.getRole() == null) {
            throw notAllowed();
        }
    }

    private void checkNotDeleted(Ticket ticket) {
        if (ticket.isDeleted()) {
            throw new BadRequestExeption(
                    ExceptionMessage.TICKET_IS_DELETED
            );
        }
    }

    private void checkNotClosed(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BadRequestExeption(
                    ExceptionMessage.TICKET_IS_CLOSED
            );
        }
    }

    private void checkProfileOwner(Attachment attachment, User user) {
        if (attachment == null || user.getId() == null) {
            throw notAllowed();
        }

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (attachment.getUser() == null
                || attachment.getUser().getId() == null
                || !user.getId().equals(attachment.getUser().getId())) {
            throw notAllowed();
        }
    }

    private void checkTicketOwner(Ticket ticket, User user) {
        if (user.getId() == null) {
            throw notAllowed();
        }

        switch (user.getRole()) {
            case ADMIN:
            case IT_MANAGER:
                return;

            case EMPLOYEE:
                if (ticket.getCreatedBy() == null
                        || !user.getId().equals(
                                ticket.getCreatedBy().getId())) {
                    throw notAllowed();
                }
                return;

            case IT_SUPPORT:
                if (ticket.getAssignedTo() == null
                        || ticket.getAssignedTo().getId() == null
                        || !user.getId().equals(
                                ticket.getAssignedTo().getId())) {
                    throw notAllowed();
                }
                return;

            default:
                throw notAllowed();
        }
    }

    private void checkTicketAccess(Ticket ticket, User user) {
        if (user.getId() == null) {
            throw notAllowed();
        }

        switch (user.getRole()) {
            case ADMIN:
            case IT_MANAGER:
                return;

            case EMPLOYEE:
                if (ticket.getCreatedBy() == null
                        || !user.getId().equals(
                                ticket.getCreatedBy().getId())) {
                    throw notAllowed();
                }
                return;

            case IT_SUPPORT:
                boolean createdBySupport =
                        ticket.getCreatedBy() != null
                        && user.getId().equals(
                                ticket.getCreatedBy().getId());

                boolean assignedToSupport =
                        ticket.getAssignedTo() != null
                        && user.getId().equals(
                                ticket.getAssignedTo().getId());

                if (!createdBySupport && !assignedToSupport) {
                    throw notAllowed();
                }
                return;

            default:
                throw notAllowed();
        }
    }

    private void checkAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw notAllowed();
        }
    }

    private void checkAdminOrManager(User user) {
        if (user.getRole() != Role.ADMIN
                && user.getRole() != Role.IT_MANAGER) {
            throw notAllowed();
        }
    }

    private void checkAdminManagerOrSupport(User user) {
        if (user.getRole() != Role.ADMIN
                && user.getRole() != Role.IT_MANAGER
                && user.getRole() != Role.IT_SUPPORT) {
            throw notAllowed();
        }
    }

    private void checkClosed(Ticket ticket) {
        if (ticket.getStatus() != TicketStatus.CLOSED) {
            throw new BadRequestExeption(
                    ExceptionMessage.TICKET_NOT_CLOSED
            );
        }
    }

    private BadRequestExeption notAllowed() {
        return new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
    }
}
