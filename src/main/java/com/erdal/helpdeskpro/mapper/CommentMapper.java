package com.erdal.helpdeskpro.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;

public class CommentMapper {

	public static Comment commentDTOtoComment(CommentDTO commentDTO) {

		User user = new User();
		user.setId(commentDTO.getAuthorId());
	
		
		Ticket ticket = new Ticket();
		ticket.setId(commentDTO.getTicketId());
		
		Comment comment = new Comment();

		comment.setId(commentDTO.getId());
		comment.setAuthor(user);
		comment.setTicket(ticket);
		comment.setId(commentDTO.getId());
		comment.setId(commentDTO.getId());
		
		
		return comment;

	}
	public static CommentDTO commentoCommentdto(Comment comment) {
		
		User user = new User();

		Ticket ticket = new Ticket();
		
		CommentDTO commentDTO = new CommentDTO();
		
		commentDTO.setId(comment.getId());
		commentDTO.setAuthorId(user.getId());
		commentDTO.setTicketId(ticket.getId());
		commentDTO.setContent(comment.getContent());
		commentDTO.setDeleted(comment.isDeleted());
		
		
		return commentDTO;
		
	}
	
		
		public static List<CommentDTO> commentListToCommentDTOList(List<Comment> comments) {

		    if (comments == null || comments.isEmpty()) {
		        return Collections.emptyList();
		    }

		    return comments.stream()
		            .map(comment -> {
		                CommentDTO dto = new CommentDTO();
		                dto.setId(comment.getId());
		                dto.setContent(comment.getContent());
		                dto.setAuthorId(comment.getAuthor().getId());
		                dto.setTicketId(comment.getTicket().getId());
		                return dto;
		            })
		            .collect(Collectors.toList());
		}
		
		
		
	
		public static List<Comment> commentDTOListToCommentList(List<CommentDTO> commentDTOs) {

		    if (commentDTOs == null || commentDTOs.isEmpty()) {
		        return Collections.emptyList();
		    }
		    User user = new User();
		
			Ticket ticket = new Ticket();
			
		    return commentDTOs.stream()
		            .map(commentDTO -> {
		                Comment comment = new Comment();
		                user.setId(commentDTO.getAuthorId());
		                ticket.setId(commentDTO.getTicketId());
		                comment.setId(commentDTO.getId());
		                comment.setContent(commentDTO.getContent());
		                comment.setAuthor(user);
		                comment.setTicket(ticket);
		                return comment;
		            })
		            .collect(Collectors.toList());
		}
		
	

}