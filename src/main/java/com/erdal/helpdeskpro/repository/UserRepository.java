package com.erdal.helpdeskpro.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.erdal.helpdeskpro.config.DatabaseConnect;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.Role;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;

public class UserRepository {

	// The database generates the primary key, and I retrieve it using JDBC
	// generated keys.
	// I use try-with-resources to ensure connections and statements are properly
	// closed.
	public User save(User user) {

		String sql = """
				INSERT INTO users (username, email, password, role, is_active, created_at)
				VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DatabaseConnect.connect();
				PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

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

		try (Connection conn = DatabaseConnect.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				User user = new User(
						rs.getLong("id"), 
						rs.getString("username"), 
						rs.getString("email"),
						rs.getString("password"), Role.valueOf(rs.getString("role")), 
						rs.getBoolean("is_active"),
						rs.getTimestamp("created_at").toLocalDateTime() // ✅ doğru dönüşüm
				);

				return Optional.of(user);
				
			}

		} catch (Exception e) {
			System.out.println("FindById Error : " + e.getMessage());
		}

		return Optional.empty();
		
	}

	public List<User> findAll() {

		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM users ORDER BY username";

		try (Connection conn = DatabaseConnect.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {

				User user = new User(
						rs.getLong("id"), 
						rs.getString("username"), 
						rs.getString("email"),
						rs.getString("password"), 
						Role.valueOf(rs.getString("role")), 
						rs.getBoolean("is_active"),
						rs.getTimestamp("created_at").toLocalDateTime());

				users.add(user);
			}

		} catch (SQLException e) {
			System.out.println("User list error: " + e.getMessage());
			e.printStackTrace();
		}

		return users;
	}

	public User findByEmail(String email) {
		return null;
	};

	public boolean existsByEmail(String email) {
		return true;
	};

}
