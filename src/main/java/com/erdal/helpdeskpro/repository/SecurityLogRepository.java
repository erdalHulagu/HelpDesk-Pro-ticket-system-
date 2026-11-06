
package com.erdal.helpdeskpro.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.SecurityLog;

public interface SecurityLogRepository {

	SecurityLog save(SecurityLog securityLog);

	List<SecurityLog> findAll();

	List<SecurityLog> findByUsername(String username);

	List<SecurityLog> findByAction(String action);

	List<SecurityLog> findAllByCreatedAtRange(LocalDateTime startDate, LocalDateTime endDate);
}
