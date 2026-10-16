
package com.erdal.helpdeskpro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketPriority;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.TicketRepository;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private Authorization authorization;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ticketService = new TicketServiceImpl(
            ticketRepository,
            authorization
        );
    }

    // 1. Ticket oluşturulurken varsayılan değerler atanmalı.
    @Test
    void createTicket_shouldSetDefaultValuesAndSaveTicket() {

        Ticket ticket = new Ticket();
        User currentUser = new User();

        ticketService.createTicket(ticket, currentUser);

        assertSame(currentUser, ticket.getCreatedBy());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());
        assertFalse(ticket.isDeleted());

        verify(ticketRepository).save(ticket);
    }

    // 2. Ticket ID ile bulunamazsa hata fırlatılmalı.
    @Test
    void getTicketById_shouldThrowExceptionWhenTicketNotFound() {

        Long ticketId = 99L;
        User currentUser = new User();

        when(ticketRepository.findById(ticketId)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> ticketService.getTicketById(ticketId, currentUser)
        );
    }

    // 3. ADMIN tüm ticket'ları alabilmeli.
    @Test
    void getAllTickets_shouldReturnAllTicketsForAdmin() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        List<Ticket> expectedTickets =
            List.of(new Ticket(), new Ticket());

        when(ticketRepository.findAll()).thenReturn(expectedTickets);

        List<Ticket> actualTickets = ticketService.getAllTickets(admin);

        assertEquals(2, actualTickets.size());
        assertEquals(expectedTickets, actualTickets);

        verify(ticketRepository).findAll();
    }

    // 4. EMPLOYEE için yalnızca kendi ID'siyle sorgulama yapılmalı.
    @Test
    void getAllTickets_shouldReturnOwnTicketsForEmployee() {

        User employee = new User();
        employee.setId(10L);
        employee.setRole(Role.EMPLOYEE);

        List<Ticket> expectedTickets =
            List.of(new Ticket(), new Ticket());

        when(ticketRepository.findAllActiveByCreatedBy(10L))
            .thenReturn(expectedTickets);

        List<Ticket> actualTickets = ticketService.getAllTickets(employee);

        assertEquals(2, actualTickets.size());
        assertEquals(expectedTickets, actualTickets);

        verify(ticketRepository).findAllActiveByCreatedBy(10L);
        verify(ticketRepository, never()).findAll();
    }

    // 5. ADMIN olmayan kullanıcı yönetici listeleme metodunu kullanamamalı.
    @Test
    void getAllTicketsForAdmin_shouldThrowExceptionForEmployee() {

        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        assertThrows(
            BadRequestExeption.class,
            () -> ticketService.getAllTicketsForAdmin(employee)
        );

        verifyNoInteractions(ticketRepository);
    }

    // 6. Ticket bulunamazsa silme işlemi hata vermeli.
    @Test
    void deleteTicket_shouldThrowExceptionWhenTicketNotFound() {

        Long ticketId = 99L;

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(ticketId)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> ticketService.deleteTicket(ticketId, admin)
        );

        verify(ticketRepository, never()).update(any(Ticket.class));
    }

    // 7. Silme işlemi fiziksel silme yerine soft delete uygulamalı.
    @Test
    void deleteTicket_shouldMarkTicketAsDeleted() {

        Long ticketId = 1L;

        Ticket ticket = new Ticket();
        ticket.setDeleted(false);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(ticketId)).thenReturn(ticket);

        ticketService.deleteTicket(ticketId, admin);

        assertTrue(ticket.isDeleted());

        verify(authorization).canDeleteTicket(ticket, admin);
        verify(ticketRepository).update(ticket);
    }

    // 8. OPEN durumundan doğrudan CLOSED durumuna geçilememeli.
    @Test
    void updateStatus_shouldThrowExceptionForInvalidTransition() {

        Long ticketId = 1L;

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(ticketId)).thenReturn(ticket);

        assertThrows(
            BadRequestExeption.class,
            () -> ticketService.updateStatus(
                ticketId,
                TicketStatus.CLOSED,
                admin
            )
        );

        verify(authorization).canUpdateStatus(ticket, admin);
        verify(ticketRepository, never()).update(any(Ticket.class));
    }

    // 9. OPEN -> IN_PROGRESS geçişi başarılı olmalı.
    @Test
    void updateStatus_shouldUpdateTicketWhenTransitionIsValid() {

        Long ticketId = 1L;

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(ticketId)).thenReturn(ticket);

        ticketService.updateStatus(
            ticketId,
            TicketStatus.IN_PROGRESS,
            admin
        );

        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());

        verify(authorization).canUpdateStatus(ticket, admin);
        verify(ticketRepository).update(ticket);
    }

    // 10. Yeni durum null ise güncelleme reddedilmeli.
    @Test
    void updateStatus_shouldThrowExceptionWhenNewStatusIsNull() {

        Long ticketId = 1L;

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(ticketId)).thenReturn(ticket);

        assertThrows(
            BadRequestExeption.class,
            () -> ticketService.updateStatus(ticketId, null, admin)
        );

        verify(ticketRepository, never()).update(any(Ticket.class));
    }

    // 11. Güncellenecek ticket bulunamazsa hata fırlatılmalı.
    @Test
    void updateStatus_shouldThrowExceptionWhenTicketNotFound() {

        Long ticketId = 99L;

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(ticketId)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> ticketService.updateStatus(
                ticketId,
                TicketStatus.IN_PROGRESS,
                admin
            )
        );

        verify(ticketRepository, never()).update(any(Ticket.class));
    }
}
