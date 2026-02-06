package com.erdal.helpdeskpro.repository;



import java.util.List;

import com.erdal.helpdeskpro.domain.Comment;

public interface CommonRepository {
	
	public void save();
	
	public Comment findById(Long id);
	
	public List<Comment> findAll();
	
	public void delete(Long id);
	
	
	
}
