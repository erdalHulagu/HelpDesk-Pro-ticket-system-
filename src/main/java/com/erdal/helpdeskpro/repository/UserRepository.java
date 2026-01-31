package com.erdal.helpdeskpro.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.erdal.helpdeskpro.config.DatabaseConnect;
import com.erdal.helpdeskpro.domain.User;

public class UserRepository {
	
		
	//The database generates the primary key, and I retrieve it using JDBC generated keys.
	//I use try-with-resources to ensure connections and statements are properly closed.
	public User save(User user) {

	    String sql = """
	        INSERT INTO users (username, email, password, role, is_active, created_at)
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
	        ps.setString(4, user.getRole() != null ? user.getRole() : "EMPLOYEE");
	        ps.setBoolean(5, user.isActive());
	        ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));

	        ps.executeUpdate();

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
	
	
	public Optional<User> findById(Long id) {
		
		String sql = "SELECT * FROM users WHERE id = ?";
				;
		
		try(Connection conn=DatabaseConnect.connect();
				PreparedStatement pStatement=conn.prepareStatement(sql)) {
			pStatement.setLong(1, id);
			ResultSet resultSet =pStatement.executeQuery();
			if (resultSet.next()) {
				
				return Optional.of(new User(
						resultSet.getLong("id"),
						resultSet.getString("username"),
						resultSet.getString("email"),
						resultSet.getString("password"),
						resultSet.getString("role"),
						resultSet.getBoolean("isActive"),
						resultSet.getTimestamp(Timestamp.valueOf("createdAt"))
						));
				
			}
			
		} catch (Exception e) {
			System.out.println("FindById Error : " + e.getMessage());
		}
		
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
