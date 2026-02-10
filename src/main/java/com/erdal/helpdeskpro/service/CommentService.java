package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.User;

public interface CommentService {
	
	void createComment(Comment comment,User user);
	
	Comment findCommentById(Long commentId);
	
	List<Comment> findAllComments();
	
	void deleteCommentById(Long commentId);
	

}
