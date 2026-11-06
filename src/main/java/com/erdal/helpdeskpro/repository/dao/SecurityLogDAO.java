
package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.SecurityLog;
import com.erdal.helpdeskpro.repository.SecurityLogRepository;
import java.time.LocalDateTime;

public class SecurityLogDAO implements SecurityLogRepository {

	private final SessionFactory sessionFactory;

	public SecurityLogDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public SecurityLog save(SecurityLog securityLog) {
		Transaction transaction = null;

		try (Session session = sessionFactory.openSession()) {
			try {
				transaction = session.beginTransaction();

				session.persist(securityLog);

				transaction.commit();

				return securityLog;

			} catch (RuntimeException e) {
				if (transaction != null && transaction.isActive()) {
					transaction.rollback();
				}
				throw e;
			}
		}
	}

	@Override
	public List<SecurityLog> findAll() {
		try (Session session = sessionFactory.openSession()) {
			return session.createQuery("from SecurityLog s " + "left join fetch s.user " + "order by s.createdAt desc",
					SecurityLog.class).getResultList();
		}
	}

	@Override
	public List<SecurityLog> findAllByCreatedAtRange(LocalDateTime startDate, LocalDateTime endDate) {

		try (Session session = sessionFactory.openSession()) {

			StringBuilder hql = new StringBuilder("from SecurityLog s " + "left join fetch s.user " + "where 1=1");

			if (startDate != null) {
				hql.append(" and s.createdAt >= :startDate");
			}

			if (endDate != null) {
				hql.append(" and s.createdAt <= :endDate");
			}

			hql.append(" order by s.createdAt desc");

			var query = session.createQuery(hql.toString(), SecurityLog.class);

			if (startDate != null) {
				query.setParameter("startDate", startDate);
			}

			if (endDate != null) {
				query.setParameter("endDate", endDate);
			}

			return query.getResultList();
		}
	}

	@Override
	public List<SecurityLog> findByUsername(String username) {
		try (Session session = sessionFactory.openSession()) {
			return session
					.createQuery(
							"from SecurityLog s " + "left join fetch s.user "
									+ "where lower(s.username) like lower(:username) " + "order by s.createdAt desc",
							SecurityLog.class)
					.setParameter("username", "%" + username + "%").getResultList();
		}
	}

	@Override
	public List<SecurityLog> findByAction(String action) {
		try (Session session = sessionFactory.openSession()) {
			return session
					.createQuery("from SecurityLog s " + "left join fetch s.user " + "where s.action = :action "
							+ "order by s.createdAt desc", SecurityLog.class)
					.setParameter("action", action).getResultList();
		}
	}
}
