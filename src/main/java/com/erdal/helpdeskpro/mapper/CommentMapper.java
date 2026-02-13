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
	 public static CommentDTO commentToCommentDTO(Comment comment) {
	        CommentDTO dto = new CommentDTO();

	        dto.setId(comment.getId());
	        dto.setContent(comment.getContent());
	        dto.setAuthorId(comment.getAuthor().getId());
	        dto.setTicketId(comment.getTicket().getId());
	        dto.setDeleted(comment.isDeleted());

	        return dto;
	    }

	
	public static List<CommentDTO> commentListToCommentDTOList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return Collections.emptyList();
        return comments.stream()
                .map(CommentMapper::commentToCommentDTO)
                .collect(Collectors.toList());
    }

    public static List<Comment> commentDTOListToCommentList(List<CommentDTO> commentDTOs) {
        if (commentDTOs == null || commentDTOs.isEmpty()) return Collections.emptyList();
        return commentDTOs.stream()
                .map(CommentMapper::commentDTOtoComment)
                .collect(Collectors.toList());
    }
	

}