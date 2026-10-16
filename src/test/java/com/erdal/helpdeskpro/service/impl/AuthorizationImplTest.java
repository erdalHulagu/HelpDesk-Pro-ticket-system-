package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.authorization.AuthorizationImpl;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationImplTest {

    private AuthorizationImpl authorization;

    private User admin;
    private User employee;
    private User support;
    private User anotherEmployee;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        authorization = new AuthorizationImpl();

        admin = createUser(1L, Role.ADMIN);
        employee = createUser(2L, Role.EMPLOYEE);
        support = createUser(3L, Role.IT_SUPPORT);
        anotherEmployee = createUser(4L, Role.EMPLOYEE);

        ticket = createTicket();
    }

    private User createUser(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private Ticket createTicket() {
        Ticket newTicket = new Ticket();
        newTicket.setId(10L);
        newTicket.setCreatedBy(employee);
        newTicket.setAssignedTo(support);
        newTicket.setDeleted(false);
        newTicket.setStatus(TicketStatus.OPEN);
        return newTicket;
    }

    // =========================================
    // canViewTicket
    // =========================================

    @Test
    void canViewTicket_allowsAdmin() {
        assertDoesNotThrow(() ->
                authorization.canViewTicket(ticket, admin));
    }

    @Test
    void canViewTicket_allowsTicketOwner() {
        assertDoesNotThrow(() ->
                authorization.canViewTicket(ticket, employee));
    }

    @Test
    void canViewTicket_allowsAssignedSupport() {
        assertDoesNotThrow(() ->
                authorization.canViewTicket(ticket, support));
    }

    @Test
    void canViewTicket_rejectsEmployeeWhoDoesNotOwnTicket() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, anotherEmployee));
    }

    @Test
    void canViewTicket_rejectsNullTicket() {
        assertThrows(ResourceNotFoundExeption.class, () ->
                authorization.canViewTicket(null, employee));
    }

    @Test
    void canViewTicket_rejectsNullUser() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, null));
    }

    @Test
    void canViewTicket_rejectsUserWithoutRole() {
        User user = createUser(5L, null);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, user));
    }

    @Test
    void canViewTicket_rejectsDeletedTicket() {
        ticket.setDeleted(true);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, employee));
    }

    @Test
    void canViewTicket_rejectsEmployeeWhenTicketHasNoOwner() {
        ticket.setCreatedBy(null);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, employee));
    }

    @Test
    void canViewTicket_rejectsSupportWhenTicketHasNoAssignee() {
        ticket.setAssignedTo(null);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, support));
    }

    @Test
    void canViewTicket_rejectsUserWithoutId() {
        User userWithoutId = createUser(null, Role.EMPLOYEE);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewTicket(ticket, userWithoutId));
    }

    // =========================================
    // canUpdateStatus
    // =========================================

    @Test
    void canUpdateStatus_allowsAdmin() {
        assertDoesNotThrow(() ->
                authorization.canUpdateStatus(ticket, admin));
    }

    @Test
    void canUpdateStatus_allowsSupport() {
        assertDoesNotThrow(() ->
                authorization.canUpdateStatus(ticket, support));
    }

    @Test
    void canUpdateStatus_rejectsEmployee() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canUpdateStatus(ticket, employee));
    }

    @Test
    void canUpdateStatus_rejectsDeletedTicket() {
        ticket.setDeleted(true);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canUpdateStatus(ticket, admin));
    }

    @Test
    void canUpdateStatus_rejectsClosedTicket() {
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canUpdateStatus(ticket, admin));
    }

    // =========================================
    // canAssignTicket
    // =========================================

    @Test
    void canAssignTicket_allowsAdmin() {
        assertDoesNotThrow(() ->
                authorization.canAssignTicket(ticket, admin));
    }

    @Test
    void canAssignTicket_allowsSupport() {
        assertDoesNotThrow(() ->
                authorization.canAssignTicket(ticket, support));
    }

    @Test
    void canAssignTicket_rejectsEmployee() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canAssignTicket(ticket, employee));
    }

    @Test
    void canAssignTicket_rejectsDeletedTicket() {
        ticket.setDeleted(true);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canAssignTicket(ticket, admin));
    }

    @Test
    void canAssignTicket_rejectsClosedTicket() {
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canAssignTicket(ticket, admin));
    }

    // =========================================
    // canDeleteTicket
    // =========================================

    @Test
    void canDeleteTicket_allowsAdminWhenTicketIsClosed() {
        ticket.setStatus(TicketStatus.CLOSED);

        assertDoesNotThrow(() ->
                authorization.canDeleteTicket(ticket, admin));
    }

    @Test
    void canDeleteTicket_rejectsNonAdmin() {
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canDeleteTicket(ticket, support));
    }

    @Test
    void canDeleteTicket_rejectsTicketThatIsNotClosed() {
        ticket.setStatus(TicketStatus.OPEN);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canDeleteTicket(ticket, admin));
    }

    @Test
    void canDeleteTicket_rejectsDeletedTicket() {
        ticket.setDeleted(true);
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canDeleteTicket(ticket, admin));
    }

    // =========================================
    // canComment
    // =========================================

    @Test
    void canComment_allowsAdmin() {
        assertDoesNotThrow(() ->
                authorization.canComment(ticket, admin));
    }

    @Test
    void canComment_allowsTicketOwner() {
        assertDoesNotThrow(() ->
                authorization.canComment(ticket, employee));
    }

    @Test
    void canComment_allowsAssignedSupport() {
        assertDoesNotThrow(() ->
                authorization.canComment(ticket, support));
    }

    @Test
    void canComment_rejectsEmployeeWhoDoesNotOwnTicket() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canComment(ticket, anotherEmployee));
    }

    @Test
    void canComment_rejectsDeletedTicket() {
        ticket.setDeleted(true);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canComment(ticket, employee));
    }

    @Test
    void canComment_rejectsClosedTicket() {
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canComment(ticket, employee));
    }

    // =========================================
    // canViewAllActiveTickets
    // =========================================

    @Test
    void canViewAllActiveTickets_acceptsAdmin() {
        assertDoesNotThrow(() ->
                authorization.canViewAllActiveTickets(admin));
    }

    @Test
    void canViewAllActiveTickets_acceptsEmployee() {
        assertDoesNotThrow(() ->
                authorization.canViewAllActiveTickets(employee));
    }

    @Test
    void canViewAllActiveTickets_acceptsSupport() {
        assertDoesNotThrow(() ->
                authorization.canViewAllActiveTickets(support));
    }

    @Test
    void canViewAllActiveTickets_rejectsNullUser() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewAllActiveTickets(null));
    }

    @Test
    void canViewAllActiveTickets_rejectsUserWithoutRole() {
        User user = createUser(5L, null);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewAllActiveTickets(user));
    }

    // =========================================
    // canViewComments
    // =========================================

    @Test
    void canViewComments_allowsAdmin() {
        assertDoesNotThrow(() ->
                authorization.canViewComments(ticket, admin));
    }

    @Test
    void canViewComments_allowsTicketOwner() {
        assertDoesNotThrow(() ->
                authorization.canViewComments(ticket, employee));
    }

    @Test
    void canViewComments_allowsAssignedSupport() {
        assertDoesNotThrow(() ->
                authorization.canViewComments(ticket, support));
    }

    @Test
    void canViewComments_rejectsEmployeeWhoDoesNotOwnTicket() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewComments(ticket, anotherEmployee));
    }

    @Test
    void canViewComments_rejectsDeletedTicket() {
        ticket.setDeleted(true);

        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewComments(ticket, employee));
    }

    @Test
    void canViewComments_rejectsNullTicket() {
        assertThrows(ResourceNotFoundExeption.class, () ->
                authorization.canViewComments(null, employee));
    }

    @Test
    void canViewComments_rejectsNullUser() {
        assertThrows(BadRequestExeption.class, () ->
                authorization.canViewComments(ticket, null));
    }
}
