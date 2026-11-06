
package com.erdal.helpdeskpro.service;

import java.time.LocalDateTime;
import java.util.List;

import com.erdal.helpdeskpro.domain.SecurityLog;

public interface SecurityLogService {

	void log(Long userId, String username, String action, String ipAddress, String details);

	List<SecurityLog> getAllLogs();

	List<SecurityLog> searchByUsername(String username);

	List<SecurityLog> filterByAction(String action);

	List<SecurityLog> getLogsForExport(LocalDateTime startDate, LocalDateTime endDate);
}
