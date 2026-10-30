
package com.erdal.helpdeskpro.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.CommentType;
import com.erdal.helpdeskpro.enums.Role;
import com.erdal.helpdeskpro.enums.TicketStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.exception.ExceptionMessage;
import com.erdal.helpdeskpro.exception.ResourceNotFoundExeption;
import com.erdal.helpdeskpro.repository.CommentRepository;
import com.erdal.helpdeskpro.repository.TicketRepository;
import com.erdal.helpdeskpro.repository.UserRepository;
import com.erdal.helpdeskpro.service.CommentService;
import com.erdal.helpdeskpro.service.NotificationService;

public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private Authorization authorization;
    private NotificationService notificationService;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            Authorization authorization,
            NotificationService notificationService) {

        super();
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.authorization = authorization;
        this.notificationService = notificationService;
    }

    // ---------------- Create Comment ----------------

    @Override
    public Comment createComment(Comment comment, User user) {

        if (comment == null
                || comment.getTicket() == null
                || comment.getTicket().getId() == null) {

            throw new IllegalArgumentException("Ticket ID is missing");
        }

        if (user == null || user.getRole() == null || user.getId() == null) {
            throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        }

        Long ticketId = comment.getTicket().getId();

        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
        }

        if (ticket.isDeleted()) {
            throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
        }

        authorization.canComment(ticket, user);

        // Only ADMIN, IT_MANAGER and IT_SUPPORT can write INTERNAL comments.
        if (comment.getType() == CommentType.INTERNAL
                && user.getRole() != Role.ADMIN
                && user.getRole() != Role.IT_MANAGER
                && user.getRole() != Role.IT_SUPPORT) {

            throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        }

        comment.setAuthor(user);
        comment.setTicket(ticket);

        if (comment.getType() == null) {
            comment.setType(CommentType.PUBLIC);
        }

        // Save comment first.
        Comment savedComment = commentRepository.save(comment);

        // ---------------- Notification Logic ----------------

        Set<Long> recipientIds = new HashSet<>();

        List<User> users = userRepository.findAll();

        if (comment.getType() == CommentType.PUBLIC) {

            // PUBLIC comment notification rules.
            for (User recipient : users) {

                if (recipient.getId() == null
                        || recipient.getId().equals(user.getId())
                        || recipient.getRole() == null) {

                    continue;
                }

                Role recipientRole = recipient.getRole();

                switch (recipientRole) {

                    case EMPLOYEE:

                        // Employee receives notifications only
                        // for tickets created by them.
                        if (ticket.getCreatedBy() != null
                                && ticket.getCreatedBy().getId()
                                        .equals(recipient.getId())) {

                            recipientIds.add(recipient.getId());
                        }

                        break;

                    case IT_SUPPORT:

                        // IT_SUPPORT receives notifications for
                        // tickets they created or tickets assigned to them.
                        boolean isCreator = ticket.getCreatedBy() != null
                                && ticket.getCreatedBy().getId()
                                        .equals(recipient.getId());

                        boolean isAssigned = ticket.getAssignedTo() != null
                                && ticket.getAssignedTo().getId()
                                        .equals(recipient.getId());

                        if (isCreator || isAssigned) {
                            recipientIds.add(recipient.getId());
                        }

                        break;

                    case ADMIN:
                    case IT_MANAGER:

                        // ADMIN and IT_MANAGER receive notifications
                        // for all PUBLIC comments.
                        recipientIds.add(recipient.getId());

                        break;

                    default:
                        break;
                }
            }

        } else if (comment.getType() == CommentType.INTERNAL) {

            // INTERNAL comment notification rules.
            // Only ADMIN, IT_MANAGER and assigned IT_SUPPORT
            // can receive these notifications.

            for (User recipient : users) {

                if (recipient.getId() == null
                        || recipient.getId().equals(user.getId())
                        || recipient.getRole() == null) {

                    continue;
                }

                Role recipientRole = recipient.getRole();

                if (recipientRole == Role.ADMIN
                        || recipientRole == Role.IT_MANAGER) {

                    recipientIds.add(recipient.getId());
                }

                if (recipientRole == Role.IT_SUPPORT
                        && ticket.getAssignedTo() != null
                        && ticket.getAssignedTo().getId()
                                .equals(recipient.getId())) {

                    recipientIds.add(recipient.getId());
                }
            }
        }

        // Create notifications for selected recipients.
     // Create notifications for selected recipients.
        String message;
        String notificationTitle;

        if (comment.getType() == CommentType.INTERNAL) {

            // Never expose INTERNAL comment content in notifications.
            message = "New internal note on ticket #" + ticket.getId();
            notificationTitle = "New Internal Note";

        } else {

            // Include PUBLIC comment content in the notification.
            String commentContent = savedComment.getContent();

            // Keep notification message within the database column limit.
            if (commentContent != null && commentContent.length() > 300) {
                commentContent = commentContent.substring(0, 300) + "...";
            }

            message = user.getUsername()
                    + " commented on ticket #"
                    + ticket.getId()
                    + ": "
                    + commentContent;

            notificationTitle = "New Comment";
        }

        for (Long recipientId : recipientIds) {

            notificationService.createNotification(
                    recipientId,
                    ticket.getId(),
                    user.getId(),
                    "NEW_COMMENT",
                    notificationTitle,
                    message
            );
        }

        return savedComment;
    }

    // ---------------- Find Comment By Id ----------------

    @Override
    public Comment findCommentById(Long commentId) {

        Comment comment = commentRepository.findById(commentId);

        if (comment == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
        }

        if (comment.isDeleted()) {
            throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_IS_DELETED);
        }

        return comment;
    }

    // ---------------- Find All Users Comments ----------------

    @Override
    public List<Comment> findAllUsersComments(User user) {

        if (user == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.USER_NOT_FOUND);
        }

        return commentRepository.findAllByAuthorId(user.getId());
    }

    // ---------------- Delete Comment By Id ----------------

    @Override
    public void deleteCommentById(Long commentId, User user) {

        if (user == null || user.getId() == null || user.getRole() == null) {
            throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        }

        Comment comment = commentRepository.findById(commentId);

        if (comment == null || comment.isDeleted()) {
            throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
        }

        Ticket ticket = comment.getTicket();

        if (ticket == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
        }

        if (ticket.isDeleted()) {
            throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
        }

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
        }

        // Check ticket permission.
        authorization.canComment(ticket, user);

        // Role-based comment deletion permission.
        switch (user.getRole()) {

            case ADMIN:
                // ADMIN can delete all comments.
                break;

            case EMPLOYEE:

                if (comment.getAuthor() == null
                        || !user.getId().equals(comment.getAuthor().getId())) {

                    throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
                }

                break;

            case IT_SUPPORT:

                if (ticket.getAssignedTo() == null
                        || !user.getId().equals(ticket.getAssignedTo().getId())) {

                    throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
                }

                break;

            default:
                throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
        }

        // Soft delete.
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    // ---------------- Find Comment By Id With User ----------------

    @Override
    public Comment findCommentById(Long commentId, User currentUser) {

        if (currentUser == null
                || currentUser.getId() == null
                || currentUser.getRole() == null) {

            throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        }

        Comment comment = commentRepository.findById(commentId);

        if (comment == null || comment.isDeleted()) {
            throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
        }

        Ticket ticket = comment.getTicket();

        if (ticket == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
        }

        // Check ticket access permission.
        authorization.canViewComments(ticket, currentUser);

        // INTERNAL comments are visible only to ADMIN,
        // IT_MANAGER and the assigned IT_SUPPORT user.
        if (comment.getType() == CommentType.INTERNAL) {

            boolean isAdmin = currentUser.getRole() == Role.ADMIN;

            boolean isITManager = currentUser.getRole() == Role.IT_MANAGER;

            boolean isAssignedSupport =
                    currentUser.getRole() == Role.IT_SUPPORT
                    && ticket.getAssignedTo() != null
                    && ticket.getAssignedTo().getId()
                            .equals(currentUser.getId());

            if (!isAdmin && !isITManager && !isAssignedSupport) {
                throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
            }
        }

        return comment;
    }

    // ---------------- Update Comment ----------------

    @Override
    public Comment updateComment(Long commentId, String newContent, User user) {

        Comment comment = commentRepository.findById(commentId);

        if (comment == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
        }

        if (comment.isDeleted()) {
            throw new BadRequestExeption(ExceptionMessage.COMMENT_IS_DELETED);
        }

        if (comment.getTicket().getStatus() == TicketStatus.CLOSED) {
            throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
        }

        if (user.getRole() == Role.EMPLOYEE
                && !comment.getAuthor().getId().equals(user.getId())) {

            throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
        }

        if (newContent == null) {
            throw new BadRequestExeption(ExceptionMessage.CONTENT_CANNOT_BE_NULL);
        }

        String trimmedContent = newContent.trim();

        if (trimmedContent.isEmpty()) {
            throw new BadRequestExeption(ExceptionMessage.CONTENT_CANNOT_BE_EMPTY);
        }

        if (trimmedContent.length() < 3) {
            throw new BadRequestExeption(ExceptionMessage.CONTENT_TOO_SHORT);
        }

        if (trimmedContent.length() > 1000) {
            throw new BadRequestExeption(ExceptionMessage.CONTENT_TOO_LONG);
        }

        comment.setContent(trimmedContent);

        commentRepository.save(comment);

        return comment;
    }

    // ---------------- Get Comments By Ticket ----------------

    @Override
    public List<Comment> getCommentsByTicket(Long ticketId, User user) {

        if (user == null || user.getId() == null || user.getRole() == null) {
            throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
        }

        Ticket ticket = ticketRepository.findById(ticketId);

        if (ticket == null) {
            throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
        }

        if (ticket.isDeleted()) {
            throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
        }

        // Check whether the user can view this ticket's comments.
        authorization.canViewComments(ticket, user);

        // Return only comments visible to the current user.
        return commentRepository.findAllByTicketId(ticketId).stream()
                .filter(comment -> !comment.isDeleted())
                .filter(comment -> {

                    if (comment.getType() == CommentType.PUBLIC) {
                        return true;
                    }

                    if (user.getRole() == Role.ADMIN
                            || user.getRole() == Role.IT_MANAGER) {

                        return true;
                    }

                    return user.getRole() == Role.IT_SUPPORT
                            && ticket.getAssignedTo() != null
                            && ticket.getAssignedTo().getId().equals(user.getId());

                })
                .toList();
    }
}
