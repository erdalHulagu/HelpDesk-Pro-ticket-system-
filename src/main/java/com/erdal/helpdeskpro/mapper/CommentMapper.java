
package com.erdal.helpdeskpro.mapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.Attachment;
import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.Ticket;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.enums.CommentType;
import com.erdal.helpdeskpro.repository.AttachmentRepository;

public class CommentMapper {

    private final AttachmentRepository attachmentRepository;

    public CommentMapper(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public static Comment commentDTOtoComment(CommentDTO commentDTO) {
    	
        Comment comment = new Comment();

        comment.setId(commentDTO.getId());
        comment.setContent(commentDTO.getContent());

        Ticket ticket = new Ticket();
        ticket.setId(commentDTO.getTicketId());
        comment.setTicket(ticket);

        comment.setType(
            commentDTO.getType() == null
                ? CommentType.PUBLIC
                : commentDTO.getType()
        );

        return comment;
    }
    public CommentDTO commentToCommentDTO(Comment comment) {
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

        List<Map<String, Object>> attachments =
            attachmentRepository.findByCommentId(comment.getId())
                .stream()
                .map(attachment -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", attachment.getId());
                    item.put("fileName", attachment.getFileName());
                    item.put("fileType", attachment.getFileType());
                    return item;
                })
                .collect(Collectors.toList());

        dto.setAttachments(attachments);

        return dto;
    }
    public List<CommentDTO> commentListToCommentDTOList(
            List<Comment> comments) {

        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
            .map(this::commentToCommentDTO)
            .collect(Collectors.toList());
    }

    public static List<Comment> commentDTOListToCommentList(
            List<CommentDTO> commentDTOs) {

        if (commentDTOs == null || commentDTOs.isEmpty()) {
            return Collections.emptyList();
        }

        return commentDTOs.stream()
            .map(CommentMapper::commentDTOtoComment)
            .collect(Collectors.toList());
    }
}
