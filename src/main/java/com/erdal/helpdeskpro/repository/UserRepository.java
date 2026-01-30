package com.erdal.helpdeskpro.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.erdal.helpdeskpro.config.DatabaseConnect;
import com.erdal.helpdeskpro.domain.User;

public class UserRepository {
	
		
	//The database generates the primary key, and I retrieve it using JDBC generated keys.
	//I use try-with-resources to ensure connections and statements are properly closed.
	
	public User save(User user) {

	    String sql = """
	        INSERT INTO users (userName, email, password, role, is_active, created_at)
	        VALUES (?, ?, ?, ?, ?, ?)
	        """;

	    try (
	        Connection connection = DatabaseConnect.connect();
	        PreparedStatement ps = connection.prepareStatement(
	                sql,
	                PreparedStatement.RETURN_GENERATED_KEYS
	        )
	    ) {

	        ps.setString(1, user.getUsername());
	        ps.setString(2, user.getEmail());
	        ps.setString(3, user.getPassword());
	        ps.setString(4, user.getRole());
	        ps.setBoolean(5, user.isActive());
	        ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));

	        ps.executeUpdate();

	      //Sql generating key itself
	        try (ResultSet rs = ps.getGeneratedKeys()) {
	        	
	            if (rs.next()) {
	                user.setId(rs.getLong(1));
	            }
	        }

	        return user;

	    } catch (SQLException e) {
	        throw new RuntimeException("Failed to save user", e);
	    }
	}
	
	
	public User findById(Long id) {
		
		return null;
		
	};
	
	public User findByEmail(String email) {
		return null;
	};
	public boolean existsByEmail(String email) {
		return true;
	};
	
	public List<User> findAll() {
		return null;
	}

}
