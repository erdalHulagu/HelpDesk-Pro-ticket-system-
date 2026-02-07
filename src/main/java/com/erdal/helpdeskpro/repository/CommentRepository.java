package com.erdal.helpdeskpro.repository;



import java.util.List;

import com.erdal.helpdeskpro.domain.Comment;

public interface CommentRepository {
	
	 void save(Comment comment);
	
	 Comment findById(Long id);
	
	 List<Comment> findAll();
	
	 void deleteById(Long id);
	
	
	
}
