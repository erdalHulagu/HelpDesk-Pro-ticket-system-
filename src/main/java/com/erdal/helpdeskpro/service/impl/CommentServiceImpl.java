package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.authorization.Authorization;
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
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

	public CommentServiceImpl(CommentRepository commentRepository, TicketRepository ticketRepository,
			UserRepository userRepository, Authorization authorization) {
		this.commentRepository = commentRepository;
		this.ticketRepository = ticketRepository;
		this.userRepository = userRepository;
		this.authorization = authorization;

	}

	
	public CommentServiceImpl(CommentRepository commentrepository, TicketRepository ticketrepository,
			UserRepository userrepository) {
		
	}


	// --------------- Create Comment ----------------
	@Override
	public void createComment(Comment comment, User user) {

		Ticket ticket = ticketRepository.findById(comment.getTicket().getId());
		authorization.canComment(ticket, user);

		comment.setAuthor(user);
		comment.setTicket(ticket);
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

	    // Yetki kontrolü
	    if (user.getRole() == Role.EMPLOYEE &&
	        !comment.getAuthor().getId().equals(user.getId())) {

	        throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
	    }

	    comment.setDeleted(true);
	    commentRepository.save(comment);
	}
	
	// ---------------Update Comment----------------
	@Override
	public Comment updateComment(Long commentId, String newContent, User user) {
		
		Comment comment=commentRepository.findById(commentId);
		if (comment==null) {
			   throw new ResourceNotFoundExeption(ExceptionMessage.COMMENT_NOT_FOUND);
			
		}
		if (comment.isDeleted()==true) {
			
			 throw new BadRequestExeption(ExceptionMessage.COMMENT_IS_DELETED);
		}

	    if (comment.getTicket().getStatus() == TicketStatus.CLOSED) {
	        throw new BadRequestExeption(ExceptionMessage.TICKET_IS_CLOSED);
	    }
	    
	    if (user.getRole() == Role.EMPLOYEE &&
	        !comment.getAuthor().getId().equals(user.getId())) {

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

	    Ticket ticket = ticketRepository.findById(ticketId);

	    if (ticket == null) {
	        throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
	    }

	    if (ticket.isDeleted()) {
	        throw new BadRequestExeption(ExceptionMessage.TICKET_IS_DELETED);
	    }

	    // Yetki
	    if (user.getRole() == Role.EMPLOYEE &&
	        !ticket.getCreatedBy().getId().equals(user.getId())) {

	        throw new BadRequestExeption(ExceptionMessage.NO_PERMISSION);
	    }

	    return commentRepository.findAllByTicketId(ticketId);
	}
	
	


}
