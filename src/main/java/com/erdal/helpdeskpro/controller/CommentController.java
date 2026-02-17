package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.dtos.CommentDTO;
import com.erdal.helpdeskpro.mapper.CommentMapper;
import com.erdal.helpdeskpro.service.CommentService;

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // comment ekle
    public void addComment(CommentDTO dto, User user) {
        Comment comment = CommentMapper.commentDTOtoComment(dto);
        commentService.createComment(comment, user);
    }
    public CommentDTO addComment(Long commnetId ) {
    	Comment comment=commentService.findCommentById(commnetId);
    	return CommentMapper.commentToCommentDTO(comment);
    	
    }

    // ticket commentleri
    public List<CommentDTO> getCommentsByTicket(Long ticketId,User currentUser) {
        List<Comment> comments = commentService.getCommentsByTicket(ticketId,currentUser);
        return CommentMapper.commentListToCommentDTOList(comments);
    }
    //Users comments
    public List<CommentDTO> getCommentsByTicket(User currentUser) {
    	List<Comment> comments = commentService.findAllUsersComments(currentUser);
    	return CommentMapper.commentListToCommentDTOList(comments);
    }
    
    

    // comment sil
    public void deleteComment(Long commentId, User user) {
        commentService.deleteCommentById(commentId, user);
    }
   // update comment
    public CommentDTO updateComment(Long commentId,String newContent, User currentUser) {
    	Comment newComment=commentService.updateComment(commentId,newContent, currentUser);
    	return CommentMapper.commentToCommentDTO(newComment);
    }
}
