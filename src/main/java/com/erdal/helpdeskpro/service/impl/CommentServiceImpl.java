package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.CommentRepository;

public class CommentServiceImpl {

	CommentRepository commentRepository;

	public CommentServiceImpl(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;

	}
	
//	public void createComment(Comment comment,User user) {
//		
//		comment.setAuthor(user);
//	
//		
//	}

}
