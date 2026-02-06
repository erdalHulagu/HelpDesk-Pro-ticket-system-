package com.erdal.helpdeskpro.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.erdal.helpdeskpro.domain.Comment;
import com.erdal.helpdeskpro.repository.CommonRepository;

public class CommentDAO implements CommonRepository {
	
	
	SessionFactory sessionFactory;
	
	public CommentDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}

	@Override
	public void save() {
		
		
	}

	@Override
	public Comment findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Comment> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

}
