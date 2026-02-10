package com.erdal.helpdeskpro.service.impl;


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

	CommentRepository commentRepository;
	TicketRepository ticketRepository;
	UserRepository userRepository;

	public CommentServiceImpl(CommentRepository commentRepository,TicketRepository ticketRepository,UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.ticketRepository = ticketRepository;
		this.userRepository = userRepository;

	}
	
	public void createComment(Comment comment,User user) {
		
		
	
		Ticket ticket=ticketRepository.findById(comment.getTicket().getId());
		
		
		if (ticket==null) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_NOT_FOUND);
			
		}
		if (ticket.isDeleted()==true) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_IS_DELETED);
				
		}
		if (ticket.getStatus()==TicketStatus.CLOSED) {
			throw new ResourceNotFoundExeption(ExceptionMessage.TICKET_IS_CLOSED);
			
		}
		User autherUser=userRepository.findById(ticket.getCreatedBy().getId());
		
		
		if (!(user.equals(autherUser)&&user.getRole()==Role.EMPLOYEE)||
				user.getRole()==Role.EMPLOYEE) {
			throw new BadRequestExeption(ExceptionMessage.NO_PERMITION);
			
		}
		
		 if (user.getRole() == Role.EMPLOYEE &&
			        !ticket.getCreatedBy().getId().equals(user.getId())) {
			        throw new BadRequestExeption(ExceptionMessage.NO_PERMITION);
			    }

			  
			    comment.setAuthor(user);
			    comment.setTicket(ticket);
		        commentRepository.save(comment);
		
		
	}

}
