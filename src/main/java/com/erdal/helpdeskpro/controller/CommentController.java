package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.mapper.CommentMapper;
import com.erdal.helpdeskpro.service.CommentService;

public class CommentController {

	private final CommentService commentService;
	private final CommentMapper commentMapper;

	public CommentController(CommentService commentService, CommentMapper commentMapper) {
		this.commentService = commentService;
		this.commentMapper = commentMapper;
	}

	// comment ekle
	public CommentDTO addComment(CommentDTO dto, User user) {
	    Comment comment = CommentMapper.commentDTOtoComment(dto);

	    Comment savedComment = commentService.createComment(comment, user);

	    return commentMapper.commentToCommentDTO(savedComment);
	}

	public CommentDTO addComment(Long commnetId) {
		Comment comment = commentService.findCommentById(commnetId);
		return commentMapper.commentToCommentDTO(comment);

	}

	// ticket commentleri
	public List<CommentDTO> getCommentsByTicket(Long ticketId, User currentUser) {
		List<Comment> comments = commentService.getCommentsByTicket(ticketId, currentUser);
		return commentMapper.commentListToCommentDTOList(comments);
	}

	// Users comments
	public List<CommentDTO> findAllUsersComments(User currentUser) {
		List<Comment> comments = commentService.findAllUsersComments(currentUser);
		return commentMapper.commentListToCommentDTOList(comments);
	}

	// comment sil
	public void deleteComment(Long commentId, User user) {
		commentService.deleteCommentById(commentId, user);
	}

	// update comment
	public CommentDTO updateComment(Long commentId, String newContent, User currentUser) {
		Comment newComment = commentService.updateComment(commentId, newContent, currentUser);
		return commentMapper.commentToCommentDTO(newComment);
	}

	public CommentDTO getCommentById(Long commentId, User currentUser) {
		Comment comment = commentService.findCommentById(commentId, currentUser);

		return commentMapper.commentToCommentDTO(comment);
	}

}
