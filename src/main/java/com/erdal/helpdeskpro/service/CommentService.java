package com.erdal.helpdeskpro.service;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.domain.User;

public interface CommentService {
	
	public void createComment(Comment comment,User user);

}
