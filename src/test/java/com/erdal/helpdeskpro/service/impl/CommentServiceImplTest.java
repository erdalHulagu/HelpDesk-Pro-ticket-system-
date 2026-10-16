
package com.erdal.helpdeskpro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.CommentType;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;

class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authorization authorization;

    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        commentService = new CommentServiceImpl(
            commentRepository,
            ticketRepository,
            userRepository,
            authorization
        );
    }

    // 1. Yorum oluşturulurken author ve ticket atanmalı,
    // tür belirtilmediyse PUBLIC olmalı.
    @Test
    void createComment_shouldSetAuthorTicketAndDefaultType() {

        User employee = new User();
        employee.setId(1L);
        employee.setRole(Role.EMPLOYEE);

        Ticket ticket = new Ticket();
        ticket.setId(10L);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setContent("Test comment");

        when(ticketRepository.findById(10L)).thenReturn(ticket);

        commentService.createComment(comment, employee);

        assertSame(employee, comment.getAuthor());
        assertSame(ticket, comment.getTicket());
        assertEquals(CommentType.PUBLIC, comment.getType());

        verify(authorization).canComment(ticket, employee);
        verify(commentRepository).save(comment);
    }

    // 2. Yorum veya ticket bilgisi null ise işlem reddedilmeli.
    @Test
    void createComment_shouldThrowExceptionWhenTicketIsMissing() {

        Comment comment = new Comment();
        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        assertThrows(
            IllegalArgumentException.class,
            () -> commentService.createComment(comment, employee)
        );

        verifyNoInteractions(ticketRepository, commentRepository);
    }

    // 3. Kullanıcı null ise yorum oluşturulamamalı.
    @Test
    void createComment_shouldRejectNullUser() {

        Comment comment = new Comment();
        Ticket ticket = new Ticket();
        ticket.setId(10L);
        comment.setTicket(ticket);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.createComment(comment, null)
        );

        verifyNoInteractions(ticketRepository, commentRepository);
    }

    // 4. Ticket bulunamazsa hata fırlatılmalı.
    @Test
    void createComment_shouldThrowExceptionWhenTicketNotFound() {

        Ticket ticket = new Ticket();
        ticket.setId(99L);

        Comment comment = new Comment();
        comment.setTicket(ticket);

        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        when(ticketRepository.findById(99L)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.createComment(comment, employee)
        );

        verify(commentRepository, never()).save(comment);
    }

    // 5. EMPLOYEE INTERNAL yorum oluşturamamalı.
    @Test
    void createComment_shouldRejectInternalCommentFromEmployee() {

        User employee = new User();
        employee.setId(1L);
        employee.setRole(Role.EMPLOYEE);

        Ticket ticket = new Ticket();
        ticket.setId(10L);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setType(CommentType.INTERNAL);

        when(ticketRepository.findById(10L)).thenReturn(ticket);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.createComment(comment, employee)
        );

        verify(authorization).canComment(ticket, employee);
        verify(commentRepository, never()).save(comment);
    }

    // 6. Yorum ID ile bulunamazsa hata fırlatılmalı.
    @Test
    void findCommentById_shouldThrowExceptionWhenNotFound() {

        when(commentRepository.findById(99L)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.findCommentById(99L)
        );
    }

    // 7. Silinmiş yorum normal ID sorgusuyla alınamamalı.
    @Test
    void findCommentById_shouldRejectDeletedComment() {

        Comment comment = new Comment();
        comment.setDeleted(true);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.findCommentById(1L)
        );
    }

    // 8. Aktif yorum bulunduğunda aynı nesne dönmeli.
    @Test
    void findCommentById_shouldReturnActiveComment() {

        Comment comment = new Comment();
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        Comment result = commentService.findCommentById(1L);

        assertSame(comment, result);
    }

    // 9. Kullanıcı null ise kullanıcının yorumları alınamamalı.
    @Test
    void findAllUsersComments_shouldRejectNullUser() {

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.findAllUsersComments(null)
        );

        verifyNoInteractions(commentRepository);
    }

    // 10. Kullanıcının yorumları author ID üzerinden alınmalı.
    @Test
    void findAllUsersComments_shouldReturnCommentsByAuthor() {

        User user = new User();
        user.setId(5L);

        List<Comment> expected = List.of(new Comment(), new Comment());

        when(commentRepository.findAllByAuthorId(5L)).thenReturn(expected);

        List<Comment> result = commentService.findAllUsersComments(user);

        assertEquals(2, result.size());
        assertEquals(expected, result);

        verify(commentRepository).findAllByAuthorId(5L);
    }

    // 11. Yorum silerken kullanıcı bilgisi eksikse işlem reddedilmeli.
    @Test
    void deleteCommentById_shouldRejectNullUser() {

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.deleteCommentById(1L, null)
        );

        verifyNoInteractions(commentRepository);
    }

    // 12. Yorum bulunamazsa veya zaten silinmişse silme reddedilmeli.
    @Test
    void deleteCommentById_shouldThrowExceptionWhenCommentNotFound() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(commentRepository.findById(99L)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.deleteCommentById(99L, admin)
        );

        verify(commentRepository, never()).save(
            org.mockito.ArgumentMatchers.any(Comment.class)
        );
    }

    // 13. Yorumun ticket bilgisi yoksa silme yapılamamalı.
    @Test
    void deleteCommentById_shouldThrowExceptionWhenTicketIsMissing() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Comment comment = new Comment();

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.deleteCommentById(1L, admin)
        );
    }

    // 14. Silinmiş ticket üzerindeki yorum silinememeli.
    @Test
    void deleteCommentById_shouldRejectDeletedTicket() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setDeleted(true);

        Comment comment = new Comment();
        comment.setTicket(ticket);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.deleteCommentById(1L, admin)
        );

        verify(commentRepository, never()).save(comment);
    }

    // 15. CLOSED ticket üzerindeki yorum silinememeli.
    @Test
    void deleteCommentById_shouldRejectClosedTicket() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.CLOSED);

        Comment comment = new Comment();
        comment.setTicket(ticket);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.deleteCommentById(1L, admin)
        );

        verify(commentRepository, never()).save(comment);
    }

    // 16. ADMIN aktif ticket üzerindeki yorumu soft delete yapabilmeli.
    @Test
    void deleteCommentById_shouldSoftDeleteCommentForAdmin() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setDeleted(false);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        commentService.deleteCommentById(1L, admin);

        assertTrue(comment.isDeleted());

        verify(authorization).canComment(ticket, admin);
        verify(commentRepository).save(comment);
    }

    // 17. Yorum ID ile aranırken silinmiş yorum reddedilmeli.
    @Test
    void findCommentByIdWithUser_shouldRejectDeletedComment() {

        Comment comment = new Comment();
        comment.setDeleted(true);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.findCommentById(1L, admin)
        );

        verifyNoInteractions(authorization);
    }

    // 18. PUBLIC yorum, yetkilendirme kontrolünden sonra döndürülmeli.
    @Test
    void findCommentByIdWithUser_shouldReturnPublicComment() {

        User employee = new User();
        employee.setId(5L);
        employee.setRole(Role.EMPLOYEE);

        Ticket ticket = new Ticket();

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setType(CommentType.PUBLIC);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        Comment result = commentService.findCommentById(1L, employee);

        assertSame(comment, result);
        verify(authorization).canViewComments(ticket, employee);
    }

    // 19. EMPLOYEE INTERNAL yorumunu görüntüleyememeli.
    @Test
    void findCommentByIdWithUser_shouldRejectInternalCommentForEmployee() {

        User employee = new User();
        employee.setId(5L);
        employee.setRole(Role.EMPLOYEE);

        Ticket ticket = new Ticket();

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setType(CommentType.INTERNAL);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.findCommentById(1L, employee)
        );

        verify(authorization).canViewComments(ticket, employee);
    }

    // 20. Yorum güncellenirken içerik temizlenip kaydedilmeli.
    @Test
    void updateComment_shouldTrimContentAndSave() {

        User employee = new User();
        employee.setId(5L);
        employee.setRole(Role.EMPLOYEE);

        User author = new User();
        author.setId(5L);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setDeleted(false);
        comment.setContent("Old content");

        when(commentRepository.findById(1L)).thenReturn(comment);

        Comment result = commentService.updateComment(
            1L,
            "  Updated content  ",
            employee
        );

        assertSame(comment, result);
        assertEquals("Updated content", comment.getContent());

        verify(commentRepository).save(comment);
    }

    // 21. Yorum bulunamazsa güncelleme reddedilmeli.
    @Test
    void updateComment_shouldThrowExceptionWhenCommentNotFound() {

        when(commentRepository.findById(99L)).thenReturn(null);

        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.updateComment(99L, "Valid content", employee)
        );
    }

    // 22. Yeni içerik null ise güncelleme reddedilmeli.
    @Test
    void updateComment_shouldRejectNullContent() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.updateComment(1L, null, admin)
        );

        verify(commentRepository, never()).save(comment);
    }

    // 23. Boş veya yalnızca boşluklardan oluşan içerik reddedilmeli.
    @Test
    void updateComment_shouldRejectBlankContent() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.updateComment(1L, "   ", admin)
        );

        verify(commentRepository, never()).save(comment);
    }

    // 24. Üç karakterden kısa içerik reddedilmeli.
    @Test
    void updateComment_shouldRejectContentTooShort() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.updateComment(1L, "ab", admin)
        );
    }

    // 25. Bin karakterden uzun içerik reddedilmeli.
    @Test
    void updateComment_shouldRejectContentTooLong() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        String longContent = "a".repeat(1001);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.updateComment(1L, longContent, admin)
        );
    }

    // 26. CLOSED ticket üzerindeki yorum güncellenememeli.
    @Test
    void updateComment_shouldRejectClosedTicket() {

        User admin = new User();
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.CLOSED);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setDeleted(false);

        when(commentRepository.findById(1L)).thenReturn(comment);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.updateComment(1L, "Updated content", admin)
        );

        verify(commentRepository, never()).save(comment);
    }

    // 27. Ticket bulunamazsa yorum listesi alınamamalı.
    @Test
    void getCommentsByTicket_shouldThrowExceptionWhenTicketNotFound() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(ticketRepository.findById(99L)).thenReturn(null);

        assertThrows(
            ResourceNotFoundExeption.class,
            () -> commentService.getCommentsByTicket(99L, admin)
        );
    }

    // 28. Silinmiş ticket üzerindeki yorumlar listelenememeli.
    @Test
    void getCommentsByTicket_shouldRejectDeletedTicket() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setDeleted(true);

        when(ticketRepository.findById(1L)).thenReturn(ticket);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.getCommentsByTicket(1L, admin)
        );

        verifyNoInteractions(commentRepository);
    }

    // 29. EMPLOYEE yalnızca kendi ticket'ının yorumlarını görebilmeli.
    @Test
    void getCommentsByTicket_shouldRejectEmployeeForAnotherUsersTicket() {

        User employee = new User();
        employee.setId(5L);
        employee.setRole(Role.EMPLOYEE);

        User otherUser = new User();
        otherUser.setId(7L);

        Ticket ticket = new Ticket();
        ticket.setDeleted(false);
        ticket.setCreatedBy(otherUser);

        when(ticketRepository.findById(1L)).thenReturn(ticket);

        assertThrows(
            BadRequestExeption.class,
            () -> commentService.getCommentsByTicket(1L, employee)
        );

        verifyNoInteractions(commentRepository);
    }

    // 30. ADMIN aktif ticket'ın silinmemiş PUBLIC yorumlarını görebilmeli.
    @Test
    void getCommentsByTicket_shouldFilterDeletedCommentsForAdmin() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setDeleted(false);

        Comment publicComment = new Comment();
        publicComment.setType(CommentType.PUBLIC);
        publicComment.setDeleted(false);

        Comment internalComment = new Comment();
        internalComment.setType(CommentType.INTERNAL);
        internalComment.setDeleted(false);

        Comment deletedComment = new Comment();
        deletedComment.setType(CommentType.PUBLIC);
        deletedComment.setDeleted(true);

        when(ticketRepository.findById(1L)).thenReturn(ticket);
        when(commentRepository.findAllByTicketId(1L))
            .thenReturn(List.of(publicComment, internalComment, deletedComment));

        List<Comment> result = commentService.getCommentsByTicket(1L, admin);

        assertEquals(2, result.size());
        assertTrue(result.contains(publicComment));
        assertTrue(result.contains(internalComment));
        assertFalse(result.contains(deletedComment));
    }

    // 31. EMPLOYEE, kendi ticket'ındaki INTERNAL yorumları görememeli.
    @Test
    void getCommentsByTicket_shouldHideInternalCommentsFromEmployee() {

        User employee = new User();
        employee.setId(5L);
        employee.setRole(Role.EMPLOYEE);

        Ticket ticket = new Ticket();
        ticket.setDeleted(false);
        ticket.setCreatedBy(employee);

        Comment publicComment = new Comment();
        publicComment.setType(CommentType.PUBLIC);
        publicComment.setDeleted(false);

        Comment internalComment = new Comment();
        internalComment.setType(CommentType.INTERNAL);
        internalComment.setDeleted(false);

        when(ticketRepository.findById(1L)).thenReturn(ticket);
        when(commentRepository.findAllByTicketId(1L))
            .thenReturn(List.of(publicComment, internalComment));

        List<Comment> result = commentService.getCommentsByTicket(1L, employee);

        assertEquals(1, result.size());
        assertTrue(result.contains(publicComment));
        assertFalse(result.contains(internalComment));
    }
}
