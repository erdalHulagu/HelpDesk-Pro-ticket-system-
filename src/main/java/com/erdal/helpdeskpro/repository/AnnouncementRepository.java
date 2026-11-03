
package com.erdal.helpdeskpro.repository;

import java.util.List;
import java.util.Optional;
import com.erdal.helpdeskpro.domain.Announcement;
import com.erdal.helpdeskpro.enums.Role;

public interface AnnouncementRepository {

    Announcement save(Announcement announcement);

    Optional<Announcement> findById(Long id);

    List<Announcement> findAllAnnouncements();

    List<Announcement> findActiveAnnouncementsByRole(Role role);

    boolean deleteById(Long id);
}
