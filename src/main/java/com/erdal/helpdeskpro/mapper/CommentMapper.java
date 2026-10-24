
package com.erdal.helpdeskpro.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.enums.CommentType;

public class CommentMapper {

	public static Comment commentDTOtoComment(CommentDTO commentDTO) {

		Comment comment = new Comment();
		comment.setContent(commentDTO.getContent());

		Ticket ticket = new Ticket();
		ticket.setId(commentDTO.getTicketId());
		comment.setTicket(ticket);

		comment.setType(commentDTO.getType() == null ? CommentType.PUBLIC : commentDTO.getType());

		return comment;
	}


public static CommentDTO commentToCommentDTO(Comment comment) {
    CommentDTO dto = new CommentDTO();

    dto.setId(comment.getId());
    dto.setContent(comment.getContent());
    dto.setTicketId(
        comment.getTicket() != null
            ? comment.getTicket().getId()
            : null
    );
    dto.setDeleted(comment.isDeleted());
    dto.setType(comment.getType());
    dto.setCreatedAt(comment.getCreatedAt());

    User author = comment.getAuthor();

    if (author != null) {
        dto.setAuthorId(author.getId());
        dto.setAuthorUsername(author.getUsername());

        dto.setAuthorRole(
            author.getRole() != null
                ? author.getRole().name()
                : null
        );
    }

    return dto;
}


	public static List<CommentDTO> commentListToCommentDTOList(List<Comment> comments) {
		if (comments == null || comments.isEmpty()) {
			return Collections.emptyList();
		}

		return comments.stream().map(CommentMapper::commentToCommentDTO).collect(Collectors.toList());
	}

	public static List<Comment> commentDTOListToCommentList(List<CommentDTO> commentDTOs) {
		if (commentDTOs == null || commentDTOs.isEmpty()) {
			return Collections.emptyList();
		}

		return commentDTOs.stream().map(CommentMapper::commentDTOtoComment).collect(Collectors.toList());
	}
}
