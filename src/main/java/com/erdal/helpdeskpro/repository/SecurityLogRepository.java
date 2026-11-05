
package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.SecurityLog;

public interface SecurityLogRepository {

    SecurityLog save(SecurityLog securityLog);

    List<SecurityLog> findAll();

    List<SecurityLog> findByUsername(String username);

    List<SecurityLog> findByAction(String action);
}
