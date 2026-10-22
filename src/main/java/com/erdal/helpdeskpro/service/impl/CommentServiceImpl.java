package com.erdal.helpdeskpro.service.impl;

import java.util.List;

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

public class CommentServiceImpl implements CommentService {

	private CommentRepository commentRepository;
	private TicketRepository ticketRepository;
	private UserRepository userRepository;
	private Authorization authorization;

	public CommentServiceImpl(
	        CommentRepository commentRepository,
	        TicketRepository ticketRepository,
	        UserRepository userRepository,
	        Authorization authorization) {

	    this.commentRepository = commentRepository;
	    this.ticketRepository = ticketRepository;
	    this.userRepository = userRepository;
	    this.authorization = authorization;
	}

	@Override
	public void createComment(Comment comment, User user) {

		if (comment == null || comment.getTicket() == null || comment.getTicket().getId() == null) {
			throw new IllegalArgumentException("Ticket ID is missing");
		}

		if (user == null || user.getRole() == null) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		Long ticketId = comment.getTicket().getId();

		Ticket ticket = ticketRepository.findById(ticketId);

		if (ticket == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
		}

		authorization.canComment(ticket, user);

		if (comment.getType() == CommentType.INTERNAL && user.getRole() != Role.ADMIN
				&& user.getRole() != Role.IT_SUPPORT) {
			throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
		}

		comment.setAuthor(user);
		comment.setTicket(ticket);

		if (comment.getType() == null) {
			comment.setType(CommentType.PUBLIC);
		}

		commentRepository.save(comment);
	}

	// --------------- Find Comment By Id ----------------
	@Override
	public Comment findCommentById(Long commentId) {
		Comment comment = commentRepository.findById(commentId);
		if (comment == null) {

			throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
		}

		if (comment.isDeleted() == true) {
			throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_IS_DELETED);

		}

		return comment;
	}

	// --------------- Find All Users Comments ----------------
	@Override
	public List<Comment> findAllUsersComments(User user) {

		if (user == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.USER_NOT_FOUND);
		}

		return commentRepository.findAllByAuthorId(user.getId());
	}

	// ---------------Delete Comment By Id----------------

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

		// Ticket üzerindeki yetkiyi kontrol et
		authorization.canComment(ticket, user);

		// Rol bazlı yorum silme yetkisi
		switch (user.getRole()) {
		case ADMIN:
			// Tüm yorumları silebilir
			break;

		case EMPLOYEE:
			if (comment.getAuthor() == null || !user.getId().equals(comment.getAuthor().getId())) {
				throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
			}
			break;

		case IT_SUPPORT:
			if (ticket.getAssignedTo() == null || !user.getId().equals(ticket.getAssignedTo().getId())) {
				throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
			}
			break;

		default:
			throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
		}

		// Soft delete
		comment.setDeleted(true);
		commentRepository.save(comment);
	}

	@Override
	public Comment findCommentById(Long commentId, User currentUser) {

		Comment comment = commentRepository.findById(commentId);

		if (comment == null || comment.isDeleted()) {
			throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
		}

		Ticket ticket = comment.getTicket();

		// Ticket erişim yetkisini kontrol et
		authorization.canViewComments(ticket, currentUser);

		// Internal yorumları yalnızca yetkili roller görebilir
		if (comment.getType() == CommentType.INTERNAL) {
			boolean isAdmin = currentUser.getRole() == Role.ADMIN;

			boolean isAssignedSupport = currentUser.getRole() == Role.IT_SUPPORT && ticket.getAssignedTo() != null
					&& ticket.getAssignedTo().getId().equals(currentUser.getId());

			if (!isAdmin && !isAssignedSupport) {
				throw new BadRequestExeption(ExceptionMessage.NOT_ALLOWED);
			}
		}

		return comment;
	}

	// ---------------Update Comment----------------
	@Override
	public Comment updateComment(Long commentId, String newContent, User user) {

		Comment comment = commentRepository.findById(commentId);
		if (comment == null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);

		}
		if (comment.isDeleted() == true) {

			throw new BadRequestExeption(ExceptionMessage.COMMENT_IS_DELETED);
		}

		if (comment.getTicket().getStatus() == TicketStatus.CLOSED) {
			throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
		}

		if (user.getRole() == Role.EMPLOYEE && !comment.getAuthor().getId().equals(user.getId())) {

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

		// Ticket görüntüleme yetkisi
		switch (user.getRole()) {
		case ADMIN:
			break;

		case EMPLOYEE:
			if (ticket.getCreatedBy() == null || !user.getId().equals(ticket.getCreatedBy().getId())) {
				throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
			}
			break;

		case IT_SUPPORT:
			if (ticket.getAssignedTo() == null || !user.getId().equals(ticket.getAssignedTo().getId())) {
				throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
			}
			break;

		default:
			throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
		}

		// Yorum görünürlüğü
		return commentRepository.findAllByTicketId(ticketId).stream().filter(comment -> !comment.isDeleted())
				.filter(comment -> user.getRole() == Role.ADMIN || comment.getType() == CommentType.PUBLIC
						|| (user.getRole() == Role.IT_SUPPORT && comment.getType() == CommentType.INTERNAL))
				.toList();
	}

}
