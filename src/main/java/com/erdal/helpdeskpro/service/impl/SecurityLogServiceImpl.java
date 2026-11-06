package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.SecurityLog;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.repository.SecurityLogRepository;
import com.erdal.helpdeskpro.service.SecurityLogService;
import java.time.LocalDateTime;

public class SecurityLogServiceImpl implements SecurityLogService {

	private final SecurityLogRepository securityLogRepository;

	public SecurityLogServiceImpl(SecurityLogRepository securityLogRepository) {
		this.securityLogRepository = securityLogRepository;
	}

	@Override
	public void log(Long userId, String username, String action, String ipAddress, String details) {

		SecurityLog securityLog = new SecurityLog();

		securityLog.setUsername(username);
		securityLog.setAction(action);
		securityLog.setIpAddress(ipAddress);
		securityLog.setDetails(details);

		// Kayıt zamanını belirle
		securityLog.setCreatedAt(java.time.LocalDateTime.now());

		if (userId != null) {User user = new User();

			user.setId(userId);
			securityLog.setUser(user);
		}

		securityLogRepository.save(securityLog);
	}

	@Override
	public List<SecurityLog> getAllLogs() {
		return securityLogRepository.findAll();
	}
	

@Override
public List<SecurityLog> getLogsForExport(
        LocalDateTime startDate,
        LocalDateTime endDate) {

    if (startDate != null && endDate != null
            && startDate.isAfter(endDate)) {
        throw new IllegalArgumentException(
                "Start date cannot be after end date."
        );
    }

    if (startDate == null && endDate == null) {
        return securityLogRepository.findAll();
    }

    return securityLogRepository.findAllByCreatedAtRange(
            startDate,
            endDate
    );
}


	@Override
	public List<SecurityLog> searchByUsername(String username) {
		if (username == null || username.isBlank()) {
			return getAllLogs();
		}

		return securityLogRepository.findByUsername(username.trim());
	}

	@Override
	public List<SecurityLog> filterByAction(String action) {
		if (action == null || action.isBlank()) {
			return getAllLogs();
		}

		return securityLogRepository.findByAction(action.trim());
	}
}
